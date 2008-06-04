package model;
          
class DatasourceNameController {
    def scaffold = DatasourceName;
    
    def delete = {
        def datasourceName = DatasourceName.get( params.id )
        if(datasourceName) {
            try{
                datasourceName.delete(flush:true)
                flash.message = "DatasourceName ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[DatasourceName.class.getName(), datasourceName])]
                flash.errors = errors;
                redirect(action:show, id:datasourceName.id)
            }

        }
        else {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action:list)
        }
    }
}