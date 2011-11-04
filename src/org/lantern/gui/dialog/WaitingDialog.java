/*
 * This file is part of lanterna (http://code.google.com/p/lanterna/).
 * 
 * lanterna is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2010-2011 mabe02
 */

package org.lantern.gui.dialog;

import org.lantern.LanternException;
import org.lantern.gui.Action;
import org.lantern.gui.Label;
import org.lantern.gui.Panel;
import org.lantern.gui.Window;

/**
 *
 * @author mabe02
 */
public class WaitingDialog extends Window
{
    private final Thread spinThread;
    private final Label spinLabel;
    private boolean isClosed;

    public WaitingDialog(String title, String description)
    {
        super(title);
        spinLabel = new Label("-");
        final Panel panel = new Panel(Panel.Orientation.HORISONTAL);
        panel.addComponent(new Label(description));
        panel.addComponent(spinLabel);
        addComponent(panel);

        isClosed = false;
        spinThread = new Thread(new SpinCode());
    }

    protected void onVisible()
    {
        super.onVisible();
        spinThread.start();
    }
    
    public void close()
    {
        isClosed = true;
        getOwner().runInEventThread(new Action() {
            public void doAction() throws LanternException
            {
                WaitingDialog.super.close();
            }
        });
    }

    private class SpinCode implements Runnable
    {
        public void run()
        {
            while(!isClosed) {
                final String currentSpin = spinLabel.getText();
                final String nextSpin;
                if(currentSpin.equals("-"))
                    nextSpin = "\\";
                else if(currentSpin.equals("\\"))
                    nextSpin = "|";
                else if(currentSpin.equals("|"))
                    nextSpin = "/";
                else
                    nextSpin = "-";
                if(getOwner() != null) {
                    getOwner().runInEventThread(new Action() {
                        public void doAction() throws LanternException
                        {
                            spinLabel.setText(nextSpin);
                        }
                    });
                }
                try {
                    Thread.sleep(100);
                }
                catch(InterruptedException e) {}
            }
        }
    }
}
