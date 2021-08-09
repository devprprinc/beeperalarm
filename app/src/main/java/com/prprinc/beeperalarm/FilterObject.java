/*
 *     This file is part of BeeperAlarm app.
 *
 *     BeeperAlarm app is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     BeeperAlarm app is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with BeeperAlarm app.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.prprinc.beeperalarm;

import android.media.RingtoneManager;

public class FilterObject  {
    int id;
    String name;
    String phone;
    String trigger_text;
    boolean enable;
    boolean logging;
    boolean vibration;
    String ringtone_uri;
    String ringtone_name;
    int volume;

    public FilterObject()
    {
        this.id = -1;
        this.name = "";
        this.phone = "";
        this.trigger_text = "";
        this.enable = true;
        this.logging = true;
        this.vibration = true;
        this.ringtone_uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString();
        this.ringtone_name = "default";
        this.volume = 50;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTrigger_text() {
        return trigger_text;
    }

    public String getPhone() {
        return phone;
    }
}




