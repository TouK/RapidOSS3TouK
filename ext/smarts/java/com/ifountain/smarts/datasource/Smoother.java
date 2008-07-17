package com.ifountain.smarts.datasource;

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
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jul 16, 2008
 * Time: 6:21:16 PM
 */
public class Smoother extends Thread{
    private StagingArea stagingArea;
    private boolean running;
    private int refreshInterval;

    public Smoother(StagingArea stagingArea, int refreshInterval){
        this.stagingArea = stagingArea;
        this.running = true;
        this.refreshInterval = refreshInterval;
    }

    public void stopSmoother()
    {
        running = false;
    }

    public void run()
    {
        while (running)
        {
            try {
                Thread.sleep(refreshInterval);
            } catch (InterruptedException e) {
            }
            stagingArea.processedStagedNotifications();
        }
    }
}
