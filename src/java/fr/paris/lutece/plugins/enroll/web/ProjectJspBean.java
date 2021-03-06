 /*
  * @author Evan Hsia, Sofya Freyman
  */
package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.project.Project;
import fr.paris.lutece.plugins.enroll.business.project.ProjectHome;
import fr.paris.lutece.plugins.enroll.business.enrollment.Enrollment;
import fr.paris.lutece.plugins.enroll.business.enrollment.EnrollmentHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

 /**
 * This class provides the user interface to manage Project features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageProjects.jsp", controllerPath = "jsp/admin/plugins/enroll/", right = "ENROLL_MANAGEMENT" )
public class ProjectJspBean extends ManageEnrollJspBean
{
    // Templates
    private static final String TEMPLATE_MANAGE_PROJECTS = "/admin/plugins/enroll/manage_projects.html";
    private static final String TEMPLATE_CREATE_PROJECT = "/admin/plugins/enroll/create_project.html";
    private static final String TEMPLATE_MODIFY_PROJECT = "/admin/plugins/enroll/modify_project.html";
    private static final String TEMPLATE_EMAIL_ALL = "/admin/plugins/enroll/email_all.html";

    // Parameters
    protected static final String PARAMETER_ID_PROJECT = "id";
    protected static final String PARAMETER_NAME_PROJECT = "name";
    protected static final String PARAMETER_SIZE_PROJECT = "size";
    protected static final String PARAMETER_CURRENTSIZE_PROJECT = "currentsize";
    protected static final String PARAMETER_STATUS_PROJECT = "active";
    private static final String PARAMETER_EMAILS = "emails";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PROJECTS = "enroll.manage_projects.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_PROJECT = "enroll.modify_project.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PROJECT = "enroll.create_project.pageTitle";

    // Markers
    protected static final String MARK_PROJECT_LIST = "project_list";
    protected static final String MARK_PROJECT = "project";

    protected static final String JSP_MANAGE_PROJECTS = "jsp/admin/plugins/enroll/ManageProjects.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_PROJECT = "enroll.message.confirmRemoveProject";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "enroll.model.entity.project.attribute.";

    // Views
    private static final String VIEW_MANAGE_PROJECTS = "manageProjects";
    private static final String VIEW_CREATE_PROJECT = "createProject";
    private static final String VIEW_MODIFY_PROJECT = "modifyProject";

    // Actions
    private static final String ACTION_CREATE_PROJECT = "createProject";
    private static final String ACTION_MODIFY_PROJECT = "modifyProject";
    private static final String ACTION_REMOVE_PROJECT = "removeProject";
    private static final String ACTION_CONFIRM_REMOVE_PROJECT = "confirmRemoveProject";
    private static final String ACTION_CHANGE_STATUS = "doChangeProjectStatus";
    private static final String ACTION_COPY_EMAILS = "copyEmails";

    // Infos
    private static final String INFO_PROJECT_CREATED = "enroll.info.project.created";
    private static final String INFO_PROJECT_UPDATED = "enroll.info.project.updated";
    private static final String INFO_PROJECT_REMOVED = "enroll.info.project.removed";
    private static final String INFO_SIZE_IS_SMALL = "enroll.info.project.sizesmall";
    private static final String INFO_PROJECT_SAME_NAME = "enroll.info.project.samename";
    private static final String INFO_EMAILS_COPIED = "enroll.info.project.emailscopied";

    /**
     * Build the Manage View
     * @param request The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_PROJECTS, defaultView = true )
    public String getManageProjects(HttpServletRequest request)
    {
        List<Project> listProjects = ProjectHome.getProjectsList(  );
        Map<String, Object> model = getPaginatedListModel( request, MARK_PROJECT_LIST, listProjects, JSP_MANAGE_PROJECTS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_PROJECTS, TEMPLATE_MANAGE_PROJECTS, model );
    }

    /**
     * Returns the form to create a project
     *
     * @param request The Http request
     * @return the html code of the project form
     */
    @View( VIEW_CREATE_PROJECT )
    public String getCreateProject( HttpServletRequest request )
    {
        Project project = new Project();

        Map<String, Object> model = getModel(  );
        model.put( MARK_PROJECT, project );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_PROJECT, TEMPLATE_CREATE_PROJECT, model );
    }

    /**
     * Process the data capture form of a new project
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_PROJECT )
    public String doCreateProject( HttpServletRequest request )
    {
        Project project = new Project();
        populate( project, request );

        // Check constraints
        if ( !validateBean( project, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_PROJECT );
        }

        //can't add a project if name is already in use
        if (ProjectHome.findByName(project.getName()) != null ){
            addWarning( INFO_PROJECT_SAME_NAME, getLocale(  ) );
            return redirectView( request, VIEW_MANAGE_PROJECTS );
        }

        ProjectHome.create( project );
        addInfo( INFO_PROJECT_CREATED, getLocale(  ) );

        return redirectView( request, VIEW_MANAGE_PROJECTS );
    }

    @Action( ACTION_CHANGE_STATUS )
    public String doChangeProjectStatus( HttpServletRequest request )
    {
        String strIdProject = request.getParameter( PARAMETER_ID_PROJECT );
        int nIdProject = Integer.parseInt( strIdProject );
        Project project = ProjectHome.findByPrimaryKey( nIdProject );

        if ( project != null ) {
            project.flipActive();
            ProjectHome.update(project);
        }
        return redirectView( request, VIEW_MANAGE_PROJECTS );
    }

    /**
     * Manages the removal form of a project whose identifier is in the http
     * request
     *
     * @param request The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_PROJECT )
    public String getConfirmRemoveProject( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_PROJECT ) );
        url.addParameter( PARAMETER_ID_PROJECT, nId );
        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_PROJECT, url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    @Action( ACTION_COPY_EMAILS )
    public String copyEmails( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        Project project = ProjectHome.findByPrimaryKey(nId);
        String projectName = project != null ? project.getName() : "";
        String result = "";

        for (Enrollment enrollment : EnrollmentHome.getEnrollmentsForProgram(projectName)) {
            result = result + enrollment.getEmail() + ", ";
        }

        if (result.length() > 0) {
          result = result.substring(0, result.length()-2);
        }

        Map<String, Object> model = getModel(  );
        model.put( PARAMETER_EMAILS , result );
        addInfo(INFO_EMAILS_COPIED, getLocale());

        return getPage( " ", TEMPLATE_EMAIL_ALL, model );
    }

    /**
     * Handles the removal form of a project
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage projects
     */
    @Action( ACTION_REMOVE_PROJECT )
    public String doRemoveProject( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );
        Project project = ProjectHome.findByPrimaryKey(nId);

        if ( project != null) {
            //remove all the enrollments for this project
            for (Enrollment enrollment : EnrollmentHome.getEnrollmentsForProgram(project.getName())) {
                EnrollmentHome.remove(enrollment.getId());
            }
        }

        ProjectHome.remove( nId );
        addInfo( INFO_PROJECT_REMOVED, getLocale( ) );

        return redirectView( request, VIEW_MANAGE_PROJECTS );
    }

    /**
     * Returns the form to update info about a project
     *
     * @param request The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_PROJECT )
    public String getModifyProject( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_PROJECT ) );

        Project project = ProjectHome.findByPrimaryKey( nId );

        Map<String, Object> model = getModel(  );
        model.put( MARK_PROJECT, project );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_PROJECT, TEMPLATE_MODIFY_PROJECT, model );
    }

    /**
     * Process the change form of a project
     *
     * @param request The Http request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_PROJECT )
    public String doModifyProject( HttpServletRequest request ) {
        int nId = Integer.parseInt(request.getParameter(PARAMETER_ID_PROJECT));
        Project project = ProjectHome.findByPrimaryKey(nId);

        if ( project != null ) {
            String existingName = project.getName();
            populate( project, request );

            // Check constraints
            if ( !validateBean(project, VALIDATION_ATTRIBUTES_PREFIX) ) {
                return redirect( request, VIEW_MODIFY_PROJECT, PARAMETER_ID_PROJECT, project.getId() );
            }

            //first check to see if a name change is valid - cannot have two with the same name
            if ( !(existingName.equals( project.getName() ) ) ) {
                if (ProjectHome.findByName(project.getName()) != null) {
                    addWarning( INFO_PROJECT_SAME_NAME, getLocale() );
                    return redirectView(request, VIEW_MANAGE_PROJECTS);
                }
            }

            //now check to see if size constraints are ok - if so, change is ok
            if ( project.hasRoom() || project.atCapacity() ) {
                //if there was a project name change, update project enrollments
                if ( !(existingName.equals( project.getName() ) ) ) {
                    for ( Enrollment enrollment : EnrollmentHome.getEnrollmentsForProgram( existingName ) ) {
                        enrollment.setProgram( project.getName() );
                        EnrollmentHome.update( enrollment );
                    }
                }
                ProjectHome.update(project);
                addInfo( INFO_PROJECT_UPDATED, getLocale() );
                return redirectView(request, VIEW_MANAGE_PROJECTS);
            } else {
                addWarning( INFO_SIZE_IS_SMALL, getLocale() );
                return redirect( request, VIEW_MODIFY_PROJECT, PARAMETER_ID_PROJECT, project.getId() );
            }
        }
        return redirectView(request, VIEW_MANAGE_PROJECTS);
    }

    // override here changes access, allows us to mock these methods in tests
    @Override
    protected String redirectView( HttpServletRequest request, String strView ) {
        return super.redirectView( request, strView );
    }

    @Override
    protected String redirect( HttpServletRequest request, String strView, String strParameter, int nValue ) {
        return  super.redirect( request, strView, strParameter, nValue );
    }
}
