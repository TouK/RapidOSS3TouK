/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Oct 16, 2008
* Time: 1:44:15 AM
* To change this template use File | Settings | File Templates.
*/
class CompletionController {
  def codeEditorConfiguration;
  def index = {
      render(contentType: 'text/xml')
      {
        Suggestions()
      }
  }
}