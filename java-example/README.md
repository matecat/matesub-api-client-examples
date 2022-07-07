# MateSub API Java Examples

This repository contains a set of Java classes that can be used to invoke the MateSub API.

## Project Structure

The project is composed by:

- a [Main](./src/main/java/com/matesub/Main.java) class that if executed it creates a project using the information retrieved from
  the [application.properties](./src/main/resources/application.properties) file.
- a set of classes that can be used to invoke the MateSub API:
    - [JsonWebToken](./src/main/java/com/matesub/examples/JsonWebToken.java): used to request a Json Web Token that can be used to authenticate against the other API.
    - [FileUpload](./src/main/java/com/matesub/examples/FileUpload.java): used to upload a video or an SRT reference file.
    - [GetWorkspaces](./src/main/java/com/matesub/examples/GetWorkspaces.java): used to retrieve the information about the Workspaces and Folders associated with your account.
    - [GetTemplates](./src/main/java/com/matesub/examples/GetTemplates.java): used to retrieve the template information associated with the workspace.
    - [CreateProject](./src/main/java/com/matesub/examples/CreateProject.java): used to start the project creation process.
    - [AddReference](./src/main/java/com/matesub/examples/AddReference.java): used, if required, to specify a file reference for the project.
    - [AddTarget](./src/main/java/com/matesub/examples/AddTarget.java): used to add the desired targets to the project you are creating.
    - [CommitProject](./src/main/java/com/matesub/examples/CommitProject.java): used to confirm the project creation request.
    - [GetLanguages](./src/main/java/com/matesub/examples/GetLanguages.java): used to retrieve the list of accepted languages.
    - [GetProjectStatus](./src/main/java/com/matesub/examples/GetProjectStatus.java): used to retrieve the progress status of all the targets associated with a given project.
    - [GetTargetStatus](./src/main/java/com/matesub/examples/GetTargetStatus.java): used to retrieve the progress status of the given target.
    - [DownloadFile](./src/main/java/com/matesub/examples/DownloadFile.java): used to download an SRT or a VTT file.
- the requests and responses for the API are defined inside the above classes as Java Records and in the [common package](./src/main/java/com/matesub/examples/common).
- a set of utility classes:
    - [ApiKeyUtils](./src/main/java/com/matesub/examples/utils/ApiKeyUtils.java): to generate the Api Key Hash needed to request a Json Web Token to the API.
    - [JsonUtils](./src/main/java/com/matesub/examples/utils/JsonUtils.java): to define a custom ObjectMapper able to serialize and deserialize the java.time.Instant as defined by the API.
    - [PropertyUtils](./src/main/java/com/matesub/examples/utils/PropertyUtils.java): to handle the [application.properties](./src/main/resources/application.properties) file loading and reading.
- a [directory](./src/main/resources/videos) containing the videos you need to upload.
- a [directory](./src/main/resources/reference) containing the SRT file reference you need to upload.
- a [directory](./src/main/resources/downloads) where the SRT and the VTT files for your project will be downloaded.

The [OkHttp client](https://square.github.io/okhttp/) is used to make the HTTP calls to the API.

## Application Properties

To create a project using the [Main](./src/main/java/com/matesub/Main.java) class, the application needs to be configured properly.
The [application.properties](./src/main/resources/application.properties)
must be updated to pass the application the proper configuration.
Set the following properties as described:

- `api.account.email`:
  your account email. It must be the same account associated with the api key configured above.
- `api.key`:
  the API key associated with your account.
- `matesub.api.base.url`:
  this should be configured with the MateSub API base endpoint you want to use. There are two environments and related configurations (more likely you'll need to set this property to use production
  environment):
    - Production Environment: `https://api.matesub.com`
- `project.name`:
  the name of the project you are creating.
- `project.targets`:
  comma separated list of the target languages to associate to your project. Make sure those are auto-spotting languages (check the Get Languages endpoint response). Example value: `it-IT,en-US`.
- `reference.file.language`:
  the language code of the reference file defined in the `reference.file.name` property. It must be set if the `reference.file.name` property is set, leave empty otherwise.
- `reference.file.name`:
  the name of the reference file you want to upload and associate to the project you are creating. It must be present in the [reference directory](./src/main/resources/reference).
  It is optional, leave empty if you do not want a reference file to be associated with the project you are creating.
- `video.file.name`:
  the name of the video file you want to upload and associate to the project you are creating. It must be present in the [videos directory](./src/main/resources/videos).
- `video.file.source.language`:
  the source language of the video file defined in the property above.

If you need to use the test resources (video and reference) copy the desired files from the [resource directory](../resources) to the `videos` and `reference` directories and update the `application.properties` accordingly.