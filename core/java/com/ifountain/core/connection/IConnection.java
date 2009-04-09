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
/*
 * Created on Jan 26, 2006
 */
package com.ifountain.core.connection;



public interface IConnection
{
    public void init(ConnectionParam param) throws Exception;
    public ConnectionParam getParameters();
	public void _disconnect();
	public boolean isConnected();
	public boolean isValid();
	public void invalidate();
    public boolean checkConnection();
    public void setTimeout(long timeout);
    public long getTimeout();
    public boolean isConnectionException(Throwable t);
    public void _connect() throws Exception;
}
