/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
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
/**
 * Created on Jan 29, 2008
 *
 * Author Sezgin
 */
package com.ifountain.comp.exception;


public class RException extends Exception {

    private static final long serialVersionUID = 1L;

    public RException() {
        super();
    }

    public RException(String message, Throwable cause) {
        super(message, cause);
    }

    public RException(String message) {
        super(message);
    }

    public RException(Throwable cause) {
        super(cause);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof RException)
        {
            return ((RException)obj).getMessage().equals(getMessage());
        }
        return super.equals(obj);
    }
   
}
