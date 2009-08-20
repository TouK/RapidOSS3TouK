/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package http.datasource

import com.ifountain.comp.utils.HttpStatusException
import com.ifountain.comp.utils.HttpUtils
import org.apache.commons.httpclient.HttpException
import org.apache.commons.httpclient.methods.PostMethod;

class HttpUtilsMock extends HttpUtils{
        public boolean willThrowException = false;
        public List postMethodsExecuted = [];
        public String doGetRequest(String urlStr, Map params) throws HttpException, HttpStatusException, IOException {
            if(willThrowException){
                throw new HttpException();
            }
            return "<GetRequest></GetRequest>";
        }
        
        public String doPostRequest(String urlStr, Map params) throws HttpException, HttpStatusException, IOException {
            if(willThrowException){
                throw new HttpException();
            }
            return "<PostRequest></PostRequest>";
        }

    public String executePostMethod(PostMethod post) {
        postMethodsExecuted << post;
        return "<PostRequest></PostRequest>"; 
    }


    }