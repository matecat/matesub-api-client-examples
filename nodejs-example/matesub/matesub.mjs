import crypto from 'crypto';
import axios from 'axios';
import fs from 'fs';
import path from 'path';
import mime from 'mime';


var MATESUB_API_BASE_URL = process.env.MATESUB_API_BASE_URL;

const getApiKeyHash = apiKey => {
    const today = new Date().toISOString().substring(0, 10).replaceAll("-", "");
    const h1 = crypto.createHmac('sha256', Buffer.from(apiKey, "utf-8")).update(Buffer.from(today, "utf-8")).digest();
    return crypto.createHmac('sha256', Buffer.from(h1)).update(Buffer.from(apiKey, "utf-8")).digest("base64");
}

const getAuth = async (apiKeyHash, email) => {
    const body = {
        "email": email,
        "api_key_hash": apiKeyHash
    }
    const res = await axios.post(MATESUB_API_BASE_URL + '/token', body);
    return res.data;
}

const getLanguages = async jwt => {
    const url = MATESUB_API_BASE_URL + '/languages/';
    const response = await axios(
        {
            method: 'get',
            url: url,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Accept": "application/json"
            },
            maxContentLength: Infinity,
            maxBodyLength: Infinity
        }
    )
    return response.data;
}


const getLanguageByISOCode = (code, languages) => {
    let result = null;
    for (const lg of languages.autospotting) {
        if (lg.id == code) {
            result = { ...lg, autospotting: true };
            break;
        }
    }

    if (result == null) {
        for (const lg of languages.default) {
            if (lg.id == code) {
                result = { ...lg, autospotting: false };
                break;
            }
        }
    }
    return result;
}


const getTemplates = async (jwt, workspaceId) => {
    const url = MATESUB_API_BASE_URL + '/templates/' + workspaceId;
    const response = await axios(
        {
            method: 'get',
            url: url,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Accept": "application/json"
            },
            maxContentLength: Infinity,
            maxBodyLength: Infinity
        }
    )
    return response.data;
}

const uploadFile = async (jwt, filePath) => {

    let mimeType;

    switch (path.extname(filePath)) {
        case ".mp4": mimeType = "video/mp4";
            break;
        case ".srt": mimeType = "text/plain";
            break;
        default: mimeType = mime.getType(path.extname(filePath));
    }

    const url = MATESUB_API_BASE_URL + '/upload/' + path.basename(filePath);
    const response = await axios(
        {
            method: 'put',
            url: url,
            data: fs.readFileSync(filePath),
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": mimeType,
                "Accept": "application/json"
            },
            maxContentLength: Infinity,
            maxBodyLength: Infinity
        }
    )
    return response.data;

}

const createProject = async (jwt, userId, workspaceId, folderId, projectName, sourceLanguage, fileHash, fileName, guidelinesTemplate, asrService = 'stable') => {

    // create project
    let payload = {
        "workspace_id": workspaceId,
        "project": {
            "project_name": projectName,
            "source_lang": sourceLanguage,
            "file_hash": fileHash,
            "presets": {
                "asr_service": asrService,
            },
            "template": guidelinesTemplate,
            "original_file_name": fileName,
            "user_id": userId,
            "folder_id": folderId
        }
    }

    // console.log('Project creation payload:');
    // console.log(JSON.stringify(payload));

    const url = MATESUB_API_BASE_URL + '/project';
    const response = await axios(
        {
            method: 'put',
            url: url,
            data: payload,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
            }
        }
    )

    return response.data.project;
}

const addTargetsToProject = async (jwt, userId, projectId, targets) => {
    // add target(s)
    const url = MATESUB_API_BASE_URL + '/project';
    const payload = {
        "user_id": userId,
        "new_target": {
            "project_id": projectId,
            "targets": targets
        }
    }

    const response = await axios(
        {
            method: 'patch',
            url: url,
            data: payload,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        }
    )


    return response.data;

}

const addReferenceToProject = async (jwt, userId, projectId, referenceType, referenceHash, referenceLang, referenceBucket, referenceName) => {
    const payload = {
        "user_id": userId,
        "reference": {
            "project_id": projectId,
            "reference_type": referenceType,
            "file": {
                "reference_hash": referenceHash,
                "reference_lang": referenceLang,
                "reference_bucket": referenceBucket,
                "reference_name": referenceName
            }
        }
    }

    const url = MATESUB_API_BASE_URL + '/project/reference';
    const response = await axios(
        {
            method: 'patch',
            url: url,
            data: payload,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
            }
        }
    )

    return response.data.project;


}

const getWorkspaces = async jwt => {
    const url = MATESUB_API_BASE_URL + `/workspaces`;
    const response = await axios(
        {
            method: 'get',
            url: url,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
            }
        }
    )

    return response.data;
}

const getTargetSubtitles = async (jwt, exportType, targetHash) => {
    const url = MATESUB_API_BASE_URL + `/export/${exportType}/${targetHash}`;
    const response = await axios(
        {
            method: 'get',
            url: url,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
            }
        }
    )

    return response.data;
}


const getProjectStats = async (jwt, projectId) => {
    const url = MATESUB_API_BASE_URL + '/stats/project/' + projectId;
    const response = await axios(
        {
            method: 'get',
            url: url,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Accept": "application/json"
            },
            maxContentLength: Infinity,
            maxBodyLength: Infinity
        }
    )
    return response.data;
}

const getTargetStats = async (jwt, projectId, targetId) => {
    const url = MATESUB_API_BASE_URL + `/stats/project/${projectId}/${targetId}`;
    const response = await axios(
        {
            method: 'get',
            url: url,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Accept": "application/json"
            },
            maxContentLength: Infinity,
            maxBodyLength: Infinity
        }
    )
    return response.data;
}

const commitProject = async (jwt, userId, projectId) => {
    const payload = {
        "user_id": userId,
        "commit": {
            "project_id": projectId
        }
    }

    const response = await axios(
        {
            method: 'post',
            url: MATESUB_API_BASE_URL + '/project',
            data: payload,
            headers: {
                "Authorization": `Bearer ${jwt}`,
                "Content-Type": "application/json",
                "Accept": "application/json"
            }
        }
    )


    return response.data;
}


export default {
    addReferenceToProject,
    addTargetsToProject,
    commitProject,
    createProject,
    getApiKeyHash,
    getAuth,
    getLanguageByISOCode,
    getLanguages,
    getProjectStats,
    getTargetStats,
    getTargetSubtitles,
    getTemplates,
    getWorkspaces,
    uploadFile
}
