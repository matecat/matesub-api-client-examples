
import path from 'path';
import { createRequire } from "module";
import { program } from 'commander';
import chalk from 'chalk';
import fs from 'fs';
import Matesub from '../matesub/matesub.mjs';

const MATESUB_API_KEY = process.env.MATESUB_API_KEY;
const MATESUB_USER_EMAIL = process.env.MATESUB_USER_EMAIL
const require = createRequire(import.meta.url);
const netflixGuidelinesTemplate = require('../templates/netflix.json');

program
  .requiredOption("-v, --video <string>", "the path of video to be subtitled")
  .requiredOption("-s, --source-language <string>", "the video's audio ISO language code")
  .requiredOption("-t, --target-languages <string>", "the comma separated list of ISO target language codes")
  .option("-ref, --reference <string>", "the path of the SRT reference file")
  .option("-reflang, --reference-language <string>", "the reference's audio ISO language code")

program.parse();


const opts = program.opts();

// some checks on options
if (opts.reference && !opts.referenceLanguage) {
  console.error(chalk.red(`Missing reference language parameter (-reflang)`));
}
if (opts.referenceLanguage && !opts.reference) {
  console.error(chalk.red(`Missing reference file path parameter (-ref)`));
}

// reference file and language provided?
const withReference = opts.reference && opts.referenceLanguage;

// target languages
const targetLangs = opts.targetLanguages.split(',');

// the input video path
const videoPath = opts.video;

// get token for Matesub API
const apiKeyHash = Matesub.getApiKeyHash(MATESUB_API_KEY);

// get authorization
const auth = await Matesub.getAuth(apiKeyHash, MATESUB_USER_EMAIL);

// get languages
const languages = await Matesub.getLanguages(auth.jwt);



// build and check source language
const sourceLang = Matesub.getLanguageByISOCode(opts.sourceLanguage, languages.source);
if (sourceLang == null) {
  console.error(chalk.red(`Source language ${opts.sourceLanguage} not recognized or not supported`));
  process.exit();
}


// build targets and check target languages
const targets = [];
for (const lg of targetLangs) {
  let lgt = Matesub.getLanguageByISOCode(lg, languages.target);
  if (lgt == null) {
    console.error(chalk.red(`Target language ${lg} not recognized or supported`));
    process.exit();
  }
  else {
    targets.push({ target_language: lgt.id, auto_spotting: lgt.autospotting })
  }
}


// get workspace id and folder id to be used
// let's use the personal workspace
const workspaces = await Matesub.getWorkspaces(auth.jwt);
const workspace = workspaces.owned[0];
// and the first folder of that workspace
const folder = workspaces.owned[0].folders[0];

// set matesub project name
let projectName = path.basename(videoPath)

console.log(chalk.yellow(`Creating Matesub project "${projectName}" into account ${MATESUB_USER_EMAIL}`));
console.log(`- workspace\t\t\t: ${workspace.name}`);
console.log(`- folder\t\t\t: ${folder.name}`);
console.log(`- input video file\t\t: ${videoPath}`);
console.log(`- source language\t\t: ${sourceLang.id}`);
console.log(`- target language(s)\t\t: ${targetLangs}`);
console.log(`- guidelines and rules\t\t: Netflix`);
console.log();

// upload the video to Matesub
console.log(chalk.cyan('uploading video file...'));
let videoUploadResponse = await Matesub.uploadFile(auth.jwt, videoPath);

let referenceUploadResponse;
// upload reference file to Matesub if any
if (withReference) {
  console.log(chalk.cyan('upoloading reference srt file...'));
  referenceUploadResponse = await Matesub.uploadFile(auth.jwt, opts.reference);
  console.log(chalk.green('done.'));
}


console.log(chalk.cyan('creating Matesub project...'));

let project = await Matesub.createProject(
  auth.jwt,
  auth.user_id,
  workspace.workspace_id,
  folder.folder_id,
  projectName,
  sourceLang.id,
  videoUploadResponse.ETag,
  path.basename(videoPath),
  netflixGuidelinesTemplate
);

console.log(chalk.green('done.'));


console.log(chalk.cyan('adding targets...'));
await Matesub.addTargetsToProject(auth.jwt, auth.user_id, project.project_id, targets);
console.log(chalk.green('done.'));

if (withReference) {
  console.log(chalk.cyan('adding reference SRT file to project...'));
  let referenceAddResponse = await Matesub.addReferenceToProject(auth.jwt,
    auth.user_id,
    project.project_id,
    'SRT',
    referenceUploadResponse.ETag,
    opts.referenceLanguage,
    referenceUploadResponse.Bucket,
    referenceUploadResponse.Key
  );
  console.log(chalk.green('done.'));
}


console.log(chalk.cyan('committing project...'));
let projectCommitResponse = await Matesub.commitProject(auth.jwt, auth.user_id, project.project_id);
console.log(chalk.green('done.'));

console.log(chalk.yellow("Matesub editor's URLs:"))
for (const t of projectCommitResponse.targets) {
  console.log(`- ${t.target_language}\t\t: ${t.target_link}`)
}

const sleep = async ms => {
  return new Promise(resolve => {
    setTimeout(resolve, ms);
  });
}

console.log(chalk.cyan('waiting for all targets subtitles to be generated...'));
let subsReady;
do {
  let stats = await Matesub.getProjectStats(auth.jwt, project.project_id);
  process.stdout.write('.')
  // console.log(stats)
  subsReady = true;
  for (const s of stats.stats) {
    subsReady = subsReady && s.subtitling_completed;
  }

  await sleep(1000);

} while (!subsReady)

console.log(chalk.green('done.'));

console.log(chalk.cyan('downloading  and savig subtitles in VTT format...'));


for (const t of projectCommitResponse.targets) {
  let vtt = await Matesub.getTargetSubtitles(auth.jwt, 'vtt', t.target_hashes[0]);
  let vttFileName = `${projectName}__${t.target_language}.vtt`;
  console.log(vttFileName + " created.")
  fs.writeFileSync(vttFileName, vtt);
}
console.log(chalk.green('done.'));
