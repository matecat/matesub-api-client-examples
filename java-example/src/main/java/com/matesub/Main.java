package com.matesub;

import com.matesub.examples.AddReference;
import com.matesub.examples.AddReference.AddReferenceRequest.Reference;
import com.matesub.examples.AddTarget;
import com.matesub.examples.CommitProject;
import com.matesub.examples.CreateProject;
import com.matesub.examples.CreateProject.CreateProjectRequest.ProjectRequestBody;
import com.matesub.examples.DownloadFile;
import com.matesub.examples.FileUpload;
import com.matesub.examples.GetLanguages;
import com.matesub.examples.GetProjectStatus;
import com.matesub.examples.GetTargetStatus;
import com.matesub.examples.GetTemplates;
import com.matesub.examples.GetWorkspaces;
import com.matesub.examples.JsonWebToken;
import com.matesub.examples.common.Folder;
import com.matesub.examples.common.TargetStatus;
import com.matesub.examples.common.Template;
import com.matesub.examples.common.Workspace;
import com.matesub.examples.utils.ApiKeyUtils;
import com.matesub.examples.utils.PropertyUtils;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Main class. If executed it creates a project using the information retrieved from the application.properties file.
 */
public class Main {

    public static void main( String[] args ) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout( 60, TimeUnit.SECONDS )
                .writeTimeout( 60, TimeUnit.SECONDS )
                .readTimeout( 180, TimeUnit.SECONDS )
                .build();

        validateConfiguration();

        JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse = retrieveJsonWebToken( okHttpClient );
        System.out.println( "Json Web Token Retrieved: " + jsonWebTokenResponse );

        FileUpload.FileUploadResponse videoFileUploadResponse = uploadVideo( okHttpClient, jsonWebTokenResponse );
        System.out.println( "Video File Uploaded: " + videoFileUploadResponse );

        Optional<FileUpload.FileUploadResponse> referenceFileUploadResponse = uploadSrtReference( okHttpClient, jsonWebTokenResponse );
        if( referenceFileUploadResponse.isPresent() ){
            System.out.println( "Reference File Uploaded: " + referenceFileUploadResponse.get() );
        } else {
            System.out.println( "Reference File Upload Skipped." );
        }

        GetWorkspaces.GetWorkspacesResponse getWorkspacesResponse = getWorkspaces( okHttpClient, jsonWebTokenResponse );
        System.out.println( "Workspace and Folders Details Retrieved: " + getWorkspacesResponse );

        GetTemplates.GetTemplatesResponse getTemplatesResponse = getTemplates( okHttpClient, jsonWebTokenResponse, getWorkspacesResponse );
        System.out.println( "List of Templates Associated to Personal Workspace Retrieved: " + getTemplatesResponse );

        CreateProject.CreateProjectResponse createProjectResponse = createProject( okHttpClient, jsonWebTokenResponse, getWorkspacesResponse, getTemplatesResponse, videoFileUploadResponse );
        System.out.println( "Project Creation Process Started: " + createProjectResponse );

        if( referenceFileUploadResponse.isPresent() ){
            Optional<Reference> reference = addReference( okHttpClient, jsonWebTokenResponse, referenceFileUploadResponse.get(), createProjectResponse );
            reference.ifPresent( ref -> System.out.println( "Reference File Associated to the Project: " + ref ) );
        }

        CreateProject.CreateProjectResponse addTargetResponse = addTargets( okHttpClient, jsonWebTokenResponse, createProjectResponse );
        System.out.println( "Targets Added to the Project: " + addTargetResponse );

        CreateProject.CreateProjectResponse commitProjectResponse = commitProject( okHttpClient, jsonWebTokenResponse, createProjectResponse );
        System.out.println( "Project Creation Committed: " + commitProjectResponse );
        System.out.println( "Your Targets Are Available at These Links: " );
        System.out.println( commitProjectResponse.targets()
                                                 .stream()
                                                 .map( target -> " - " + target.targetLanguage() + ": " + target.targetLink() )
                                                 .reduce( "", ( s, s2 ) -> s + s2 + "\n" ) );

        waitForSubtitlingCompletion( commitProjectResponse.project().projectId(), okHttpClient, jsonWebTokenResponse.jwt() );

        downloadFiles( okHttpClient, jsonWebTokenResponse.jwt(), commitProjectResponse );
    }

    private static void validateConfiguration() throws Exception {
        System.out.println( "Validating application.properties configuration..." );

        List<String> acceptedMateSubApiBaseUrl = List.of( "https://app.matesub.com" );
        if( PropertyUtils.mateSubBaseUrl() == null || !acceptedMateSubApiBaseUrl.contains( PropertyUtils.mateSubBaseUrl() ) ){
            throw new RuntimeException( "Bad application.properties configuration. Accepted values for matesub.api.base.url: " + acceptedMateSubApiBaseUrl.stream().reduce( "", ( s, s2 ) -> s.isBlank() ? s2 : s + ", " + s2 ) );
        }

        if( PropertyUtils.videoFileName() == null || !new File( "src/main/resources/videos/" + PropertyUtils.videoFileName() ).isFile() ){
            throw new RuntimeException( "Video File not found in src/main/resources/videos directory" );
        }

        UUID apiKey = null;

        if( PropertyUtils.apiKey() != null && !PropertyUtils.apiKey().isBlank() ){
            try {
                apiKey = UUID.fromString( PropertyUtils.apiKey() );
            } catch ( Exception e ) {
                throw new RuntimeException( "Invalid api.key set." );
            }
        } else {
            throw new RuntimeException( "api.key must be set." );
        }

        String apiAccountEmail = PropertyUtils.apiAccountEmail();

        if( apiAccountEmail == null || apiAccountEmail.isBlank() ){
            throw new RuntimeException( "api.account.email must be set." );
        }

        GetLanguages.GetLanguagesResponse getLanguagesResponse = acceptedLanguages( apiKey, apiAccountEmail );
        List<String> acceptedSourceLanguages = getLanguagesResponse
                .source()
                .defaultLanguages()
                .stream()
                .map( GetLanguages.GetLanguagesResponse.Language::id )
                .toList();

        if( PropertyUtils.videoFileSourceLanguage() == null || PropertyUtils.videoFileSourceLanguage().isBlank() || !acceptedSourceLanguages.contains( PropertyUtils.videoFileSourceLanguage() ) ){
            throw new RuntimeException( "Invalid Video source language. Accepted languages are: "
                                                + acceptedSourceLanguages.stream().reduce( "", ( s, s2 ) -> s.isBlank() ? s2 : s + ", " + s2 ) );
        }

        List<String> targetAutoSpottingLanguages = getLanguagesResponse
                .target()
                .autospotting()
                .stream()
                .map( GetLanguages.GetLanguagesResponse.Language::id )
                .toList();

        if( !PropertyUtils.projectTargets().isEmpty() ){
            List<String> misConfiguredTargetLanguages = PropertyUtils.projectTargets()
                                                                     .stream()
                                                                     .filter( targetLanguage -> !targetAutoSpottingLanguages.contains( targetLanguage ) )
                                                                     .toList();
            if( !misConfiguredTargetLanguages.isEmpty() ){
                throw new RuntimeException( "Invalid target languages detected: "
                                                    + misConfiguredTargetLanguages.stream().reduce( "", ( s, s2 ) -> s.isBlank() ? s2 : s + ", " + s2 )
                                                    + ". Accepted languages are: "
                                                    + targetAutoSpottingLanguages.stream().reduce( "", ( s, s2 ) -> s.isBlank() ? s2 : s + ", " + s2 ) );
            }
        } else {
            throw new RuntimeException( "Invalid project.targets. At list one target is required." );
        }

        if( PropertyUtils.projectName() == null || PropertyUtils.projectName().isBlank() ){
            throw new RuntimeException( "project.name is required." );
        }

        if( PropertyUtils.referenceFileName().isPresent() && PropertyUtils.referenceFileLanguage().isEmpty() ){
            throw new RuntimeException( "If a reference.file.name is set, you must specify a reference.file.language." );
        }

        if( PropertyUtils.referenceFileName().isPresent() && !new File( "src/main/resources/reference/" + PropertyUtils.referenceFileName().get() ).isFile() ){
            throw new RuntimeException( "Reference File not found in src/main/resources/reference directory" );
        }

        if( PropertyUtils.referenceFileLanguage().isPresent() && PropertyUtils.referenceFileLanguage().stream().noneMatch( acceptedSourceLanguages::contains ) ){
            throw new RuntimeException( "Invalid reference.file.language set. Accepted values are: "
                                                + acceptedSourceLanguages.stream().reduce( "", ( s, s2 ) -> s.isBlank() ? s2 : s + ", " + s2 ) );
        }

        System.out.println( "application.properties configured properly!" );
    }

    private static GetLanguages.GetLanguagesResponse acceptedLanguages( UUID apiKey, String apiAccountEmail ) throws Exception {
        OkHttpClient                      okHttpClient         = new OkHttpClient();
        JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse = retrieveJsonWebToken( okHttpClient );

        return new GetLanguages( okHttpClient, jsonWebTokenResponse.jwt() ).executeCall();
    }

    private static JsonWebToken.JsonWebTokenResponse retrieveJsonWebToken( OkHttpClient okHttpClient ) throws Exception {
        String apiKeyHash = ApiKeyUtils.calculateHash( UUID.fromString( PropertyUtils.apiKey() ) );

        String email = PropertyUtils.apiAccountEmail();

        JsonWebToken.JsonWebTokenRequest jsonWebTokenRequest = new JsonWebToken.JsonWebTokenRequest( email, apiKeyHash );

        JsonWebToken jsonWebToken = new JsonWebToken( okHttpClient );

        return jsonWebToken.executeCall( jsonWebTokenRequest );
    }

    private static FileUpload.FileUploadResponse uploadVideo( OkHttpClient okHttpClient,
                                                              JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse ) throws Exception {
        FileUpload fileUpload = new FileUpload( okHttpClient, jsonWebTokenResponse.jwt() );

        return fileUpload.uploadVideo( PropertyUtils.videoFileName(), "src/main/resources/videos/" + PropertyUtils.videoFileName() );
    }

    private static Optional<FileUpload.FileUploadResponse> uploadSrtReference( OkHttpClient okHttpClient,
                                                                               JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse ) throws Exception {
        Optional<FileUpload.FileUploadResponse> response = Optional.empty();

        Optional<String> referenceFileName     = PropertyUtils.referenceFileName();
        Optional<String> referenceFileLanguage = PropertyUtils.referenceFileLanguage();

        if( referenceFileName.isPresent() && referenceFileLanguage.isPresent() ){
            FileUpload fileUpload = new FileUpload( okHttpClient, jsonWebTokenResponse.jwt() );
            response = Optional.of( fileUpload.uploadSrt( referenceFileName.get(), "src/main/resources/reference/" + referenceFileName.get() ) );
        }

        return response;
    }

    private static GetWorkspaces.GetWorkspacesResponse getWorkspaces( OkHttpClient okHttpClient,
                                                                      JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse ) throws Exception {
        GetWorkspaces getWorkspaces = new GetWorkspaces( okHttpClient, jsonWebTokenResponse.jwt() );

        return getWorkspaces.executeCall();
    }

    private static GetTemplates.GetTemplatesResponse getTemplates( OkHttpClient okHttpClient,
                                                                   JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse,
                                                                   GetWorkspaces.GetWorkspacesResponse getWorkspaces ) throws Exception {
        Workspace personalWorkspace = findPersonalWorkspace( getWorkspaces );

        return new GetTemplates( okHttpClient, jsonWebTokenResponse.jwt() ).executeCall( personalWorkspace.workspaceId() );
    }

    private static CreateProject.CreateProjectResponse createProject( OkHttpClient okHttpClient,
                                                                      JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse,
                                                                      GetWorkspaces.GetWorkspacesResponse getWorkspaces,
                                                                      GetTemplates.GetTemplatesResponse getTemplatesResponse,
                                                                      FileUpload.FileUploadResponse fileUploadResponse ) throws Exception {

        Workspace workspace = findPersonalWorkspace( getWorkspaces );
        String primaryFolderId = workspace.folders()
                                          .stream()
                                          .filter( folder -> folder.type().equals( "primary" ) )
                                          .map( Folder::folderId )
                                          .findFirst()
                                          .orElse( null );

        Template netflixTemplate = getTemplatesResponse.templates()
                                                       .stream()
                                                       .map( GetTemplates.GetTemplatesResponse.TemplateWrapper::template )
                                                       .filter( template -> template.name().equalsIgnoreCase( "netflix" ) )
                                                       .findFirst()
                                                       .orElse( null );

        ProjectRequestBody projectDetails = new ProjectRequestBody(
                PropertyUtils.projectName(),
                PropertyUtils.videoFileSourceLanguage(),
                fileUploadResponse.eTag(),
                new HashMap<>(),
                fileUploadResponse.key(),
                jsonWebTokenResponse.userId(),
                primaryFolderId,
                netflixTemplate
        );

        CreateProject.CreateProjectRequest createProjectRequest = new CreateProject.CreateProjectRequest( workspace.workspaceId(), projectDetails );

        return new CreateProject( okHttpClient, jsonWebTokenResponse.jwt() ).executeCall( createProjectRequest );
    }

    private static Workspace findPersonalWorkspace( GetWorkspaces.GetWorkspacesResponse getWorkspaces ) {
        return getWorkspaces.owned()
                            .stream()
                            .filter( workspace -> workspace.type().equals( "personal" ) )
                            .findFirst()
                            .orElse( null );
    }

    private static Optional<Reference> addReference( OkHttpClient okHttpClient,
                                                     JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse,
                                                     FileUpload.FileUploadResponse referenceFileUploadResponse,
                                                     CreateProject.CreateProjectResponse createProjectResponse ) throws Exception {
        Optional<Reference> response;

        Optional<String> referenceFileLanguage = PropertyUtils.referenceFileLanguage();

        if( referenceFileLanguage.isPresent() ){
            AddReference.AddReferenceRequest.FileReference file = new AddReference.AddReferenceRequest.FileReference(
                    referenceFileUploadResponse.eTag(),
                    referenceFileLanguage.get(),
                    referenceFileUploadResponse.bucket(),
                    referenceFileUploadResponse.key()
            );

            Reference                        reference           = new Reference( createProjectResponse.project().projectId(), "VTT", file );
            AddReference.AddReferenceRequest addReferenceRequest = new AddReference.AddReferenceRequest( jsonWebTokenResponse.userId(), reference );

            response = Optional.of( new AddReference( okHttpClient, jsonWebTokenResponse.jwt() ).executeCall( addReferenceRequest ) );
        } else {
            response = Optional.empty();
        }

        return response;
    }

    private static CreateProject.CreateProjectResponse addTargets( OkHttpClient okHttpClient,
                                                                   JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse,
                                                                   CreateProject.CreateProjectResponse createProjectResponse ) throws Exception {
        AddTarget.AddTargetRequest.NewTargetContainer newTargets = new AddTarget.AddTargetRequest.NewTargetContainer(
                createProjectResponse.project().projectId(),
                PropertyUtils.projectTargets()
                             .stream()
                             .map( target -> new AddTarget.AddTargetRequest.NewTarget( true, target ) )
                             .toList()
        );

        AddTarget.AddTargetRequest addTargetRequest = new AddTarget.AddTargetRequest( jsonWebTokenResponse.userId(), newTargets );
        return new AddTarget( okHttpClient, jsonWebTokenResponse.jwt() ).executeCall( addTargetRequest );
    }

    private static CreateProject.CreateProjectResponse commitProject( OkHttpClient okHttpClient,
                                                                      JsonWebToken.JsonWebTokenResponse jsonWebTokenResponse,
                                                                      CreateProject.CreateProjectResponse createProjectResponse ) throws Exception {

        CommitProject.CommitProjectRequest.Commit commit               = new CommitProject.CommitProjectRequest.Commit( createProjectResponse.project().projectId() );
        CommitProject.CommitProjectRequest        commitProjectRequest = new CommitProject.CommitProjectRequest( jsonWebTokenResponse.userId(), commit );

        return new CommitProject( okHttpClient, jsonWebTokenResponse.jwt() ).executeCall( commitProjectRequest );
    }

    private static void waitForSubtitlingCompletion( String projectId, OkHttpClient okHttpClient, String jwt ) throws Exception {
        GetProjectStatus getProjectStatus = new GetProjectStatus( okHttpClient, jwt );

        GetProjectStatus.GetProjectStatusResponse getProjectStatusResponse = getProjectStatus.executeCall( projectId );

        List<TargetStatus> targetStatuses = new ArrayList<>(
                getProjectStatusResponse.stats()
                                        .stream()
                                        .filter( targetStatus -> !targetStatus.subtitlingCompleted() )
                                        .toList() );

        GetTargetStatus getTargetStatus = new GetTargetStatus( okHttpClient, jwt );

        while ( !targetStatuses.isEmpty() ){
            try {
                System.out.println( "Waiting for Targets Subtitling Completion..." );
                Thread.sleep( 10000 );
                List<TargetStatus> toCheck = new ArrayList<>( targetStatuses );
                toCheck.forEach( targetStatus -> {
                    try {
                        GetTargetStatus.GetTargetStatusResponse getTargetStatusResponse = getTargetStatus.executeCall( projectId, targetStatus.targetId() );
                        if( getTargetStatusResponse.stats().subtitlingCompleted() ){
                            targetStatuses.remove( targetStatus );
                        }
                    } catch ( Exception e ) {
                        System.out.println( "Unable to retrieve target status" );
                        e.printStackTrace();
                    }
                } );
            } catch ( Exception e ) {
                System.out.println( "Error waiting for targets to complete" );
                e.printStackTrace();
            }
        }
        System.out.println( "Targets Subtitling Completed." );
    }

    private static void downloadFiles( OkHttpClient okHttpClient, String jwt, CreateProject.CreateProjectResponse commitProjectResponse ) {
        DownloadFile downloadFile = new DownloadFile( okHttpClient, jwt );

        commitProjectResponse.targets().forEach( target -> {
            try {
                downloadFile.downloadVtt( target.targetHashes().get( 0 ), target.targetLanguage() );
                downloadFile.downloadSrt( target.targetHashes().get( 0 ), target.targetLanguage() );
            } catch ( Exception e ) {
                System.out.println( "Unable to download SRT/VTT files for " + target.targetLanguage() );
                e.printStackTrace();
            }
        } );
    }
}
