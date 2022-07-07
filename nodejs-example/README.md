# MateSub API NodeJS Examples

# Installation

To run the demo script you need to:

1. intall NodeJS ver >= 16
2. [yarn](https://yarnpkg.com/) or [npm](https://www.npmjs.com/)
3. copy the .env.sample file to .env and replace the MATESUB_USER_EMAIL and MATESUB_API_KEY values with yours.

Let's assume you decided to use yarn:

```
yarn install
```



# Running the demo script

You can invoke the _create-project_ command which has the following options:

```
yarn create-project -h

Usage: create-project [options]

Options:
  -v, --video <string>                     the path of video to be subtitled
  -s, --source-language <string>           the video's audio ISO language code
  -t, --target-languages <string>          the comma separated list of ISO target language codes
  -ref, --reference <string>               the path of the SRT reference file
  -reflang, --reference-language <string>  the reference's audio ISO language code
  -h, --help                               display help for command
```

You can use or modify one of the following scipt execution examples:


The following command will create a Matesub project for the English video en-test-video01.mp4 with 2 target languages: it-IT, en-US. Right after the creation of the project the script will listen and wait for automatically generated subtitles and write them into two local files. 

```
yarn create-project -v ../resources/en-test-video01.mp4 -s en-US -t it-IT,en-US
```

The following command will create a Matesub project for the English video en-test-video01.mp4 with 2 target languages: it-IT, es-ES using the en-US reference file en-test-video01.mp4__en-US.srt. Right after the creation of the project the script will listen and wait for automatically generated subtitles and write them into two local files. 

```
yarn create-project -v ../resources/en-test-video01.mp4 -s en-US -t it-IT,es-ES -ref ../resources/en-test-video01.mp4__en-US.srt -reflang en-US
```

The following command will create a Matesub project for the Italian video it-test-video01.mp4 with 2 target languages: en-US, es-ES. Right after the creation of the project the script will listen and wait for automatically generated subtitles and write them into two local files. 


```
yarn create-project -v ../resources/it-test-video01.mp4 -s it-IT -t en-US,es-ES
```

