package fr.paris.lutece.plugins.enroll.web;

import fr.paris.lutece.plugins.enroll.business.enrollment.Enrollment;
import fr.paris.lutece.plugins.enroll.business.enrollment.EnrollmentHome;
import fr.paris.lutece.plugins.enroll.business.project.Project;
import fr.paris.lutece.plugins.enroll.business.project.ProjectHome;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;

import java.util.Map;
import java.util.Collection;
import fr.paris.lutece.util.ReferenceList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

@Controller( xpageName = "enrollment" , pageTitleI18nKey = "enroll.xpage.enrollment.pageTitle" , pagePathI18nKey = "enroll.xpage.enrollment.pagePathLabel" )
public class EnrollmentXPage extends MVCApplication {

  private static final String TEMPLATE_CREATE_ENROLLMENT="/skin/plugins/enroll/create_enrollment.html";
  private static final String TEMPLATE_ENROLLMENT_RESULT="/skin/plugins/enroll/enrollment_result.html";
  private static final String TEMPLATE_PROJECT_STATUS="/skin/plugins/enroll/project_status.html";

  // Parameters
  private static final String MARK_LIST_PROJECTS = "refListProjects";
  private static final String VIEW_ENROLLMENT = "enrollment";
  private static final String ACTION_CREATE_ENROLLMENT = "createEnrollment";
  private static final long serialVersionUID = 1L;


  @Action( ACTION_CREATE_ENROLLMENT )
  public XPage doCreateEnrollment( HttpServletRequest request )  {
      Enrollment enrollment = new Enrollment(  );
      populate( enrollment, request );
      Map<String, Object> model = getModel();

      // Check constraints
      if ( !validateBean( enrollment, getLocale( request ) ) )
      {
          model.put( "invalid", true);
          return getXPage( TEMPLATE_ENROLLMENT_RESULT, request.getLocale(), model );
      }

      Project project = ProjectHome.findByName( enrollment.getProgram() );

      if ( project != null ) {
          if ( project.canAdd() ) {
              project.setCurrentSize( project.getCurrentSize() + 1 );
              ProjectHome.update(project);
              EnrollmentHome.create( enrollment );
              model.put("success", true);
          } else {
              model.put("success", false);
              model.put("inactive", project.getActive()==0);
              model.put( "full", project.atCapacity());
          }
      } else {//could not find a project by this name - supplied project is not valid
          model.put("success", false);
          model.put("invalid", true);
      }

      return getXPage( TEMPLATE_ENROLLMENT_RESULT, request.getLocale(), model );
  }

  /**
   * Get the HTML content of the enrollment form
   *
   * @param request
   *            The request
   * @param locale
   *            The locale
   * @return The HTML content
   */
  @View( value = VIEW_ENROLLMENT , defaultView = true )
  public XPage getEnrollmentHtml(HttpServletRequest request)
  {

      Map<String, Object> model = new HashMap<>( );
      String program = request.getParameter("program");

      if ( program == null || program.isEmpty()) {//no program specified - return select list for project
          Collection<Project> listProjects = ProjectHome.getProjectsList();
          ReferenceList refListProjects = new ReferenceList();
          for (Project project : listProjects) {
              if (project.canAdd()) {
                  refListProjects.addItem(project.getId(), project.getName());
              }
          }
          model.put(MARK_LIST_PROJECTS, refListProjects);
          return getXPage(TEMPLATE_CREATE_ENROLLMENT, request.getLocale(  ), model);
      }else {//program specified; return form if it can add, else return information about project
          Project project = ProjectHome.findByName(program);
          if (project != null && project.canAdd()) {
              model.put("program", program);
              return getXPage(TEMPLATE_CREATE_ENROLLMENT, request.getLocale(  ), model);
          } else {
              if (project == null ) {
                  model.put("invalid", true);
              } else {
                  model.put("inactive", project.getActive() == 0);
                  model.put("full", project.atCapacity());
              }
              return getXPage(TEMPLATE_PROJECT_STATUS, request.getLocale(  ), model);
          }

      }
  }
}
