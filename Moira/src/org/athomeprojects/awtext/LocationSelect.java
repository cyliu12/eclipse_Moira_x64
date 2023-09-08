//// Moira - A Chinese Astrology Charting Program// Copyright (C) 2004-2015 At Home Projects//// This program is free software; you can redistribute it and/or modify// it under the terms of the GNU General Public License as published by// the Free Software Foundation; either version 2 of the License, or// (at your option) any later version.//// This program is distributed in the hope that it will be useful,// but WITHOUT ANY WARRANTY; without even the implied warranty of// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the// GNU General Public License for more details.//// You should have received a copy of the GNU General Public License// along with this program; if not, write to the Free Software// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA//package org.athomeprojects.awtext;import java.awt.Color;import java.awt.FlowLayout;import java.awt.Font;import java.awt.event.ActionEvent;import java.awt.event.ActionListener;import javax.swing.JComboBox;import javax.swing.JLabel;import javax.swing.JPanel;import org.athomeprojects.base.BaseCalendar;import org.athomeprojects.base.City;import org.athomeprojects.base.Resource;public class LocationSelect extends JPanel implements ActionListener {    private static final long serialVersionUID = -1235643065926944602L;    private boolean city_matching = false;    private double longitude, latitude;    private JComboBox country, city, zone;    private String country_selected, city_selected;    public void init(String file_name, String name)    {        country = city = zone = null;        longitude = latitude = City.INVALID;        setBackground(Color.white);        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));        City.loadCities(file_name);        if (name != null) {            JLabel label = new JLabel(name);            label.setFont(new Font(Resource.getFontName(), Font.BOLD, 14));            add(label);        }        Font font = new Font(Resource.getFontName(), Font.PLAIN, 12);        Font en_font = new Font(Resource.getEnFontName(), Font.PLAIN, 12);        {            country = new JComboBox(City.getCountryList());            addCombo(country, font, Resource.getString("tip_select_country"));            country_selected = getCountryName();        }        {            city = new JComboBox(City.getCityList(getCountryName()));            addCombo(city, font, Resource.getString("tip_select_city"));            city.setEditable(true);            city_selected = getCityName();        }        {            zone = new JComboBox(City.getAllZoneNames());            addCombo(zone, en_font, Resource.getString("tip_select_zone"));            int[] date = new int[5];            BaseCalendar.getCalendar(null, date);            setZone(country_selected, city_selected);        }    }    private void addCombo(JComboBox combo, Font font, String tool_tip)    {        combo.setFont(font);        combo.setBackground(Color.white);        combo.addActionListener(this);        combo.setToolTipText(tool_tip);        add(combo);    }    public String getCountryName()    {        return (String) country.getItemAt(getSelectionIndex(country));    }    public String getCityName()    {        int index = getSelectionIndex(city);        if (index >= 0)            return (String) city.getItemAt(index);        else            return (String) city.getSelectedItem();    }    public String getZoneName()    {        return (String) zone.getItemAt(getSelectionIndex(zone));    }    public double getLongitude()    {        City c = City.matchCity(getCityName(), getCountryName(), false);        if (c != null)            return c.getLongitude();        else            return longitude;    }    public double getLatitude()    {        City c = City.matchCity(getCityName(), getCountryName(), false);        if (c != null)            return c.getLatitude();        else            return latitude;    }    public void setCountryName(String name)    {        setComboName(country, name);    }    public void setCityName(String name)    {        setComboName(city, name);    }    public void setZoneName(String name)    {        setComboName(zone, name);    }    private void setComboName(JComboBox combo, String name)    {        int n = combo.getItemCount();        for (int i = 0; i < n; i++) {            String str = (String) combo.getItemAt(i);            if (str.equals(name)) {                combo.setSelectedIndex(i);                updateFields();                return;            }        }        if (combo == city) {            combo.setSelectedItem(name);            if (parseLongLatText(combo, true))                matchCity();        }    }    private boolean matchCity()    {        City match = City.matchCity(longitude, latitude, City.MATCH_ERROR_SQ);        if (match != null) {            city_matching = true;            setComboName(country, match.getCountryName());            setComboName(city, match.getCityName());            city_matching = false;            return true;        } else {            return false;        }    }    protected int matchSelection(JComboBox combo)    {        String key = ((String) combo.getSelectedItem());        int n = combo.getItemCount();        for (int i = 0; i < n; i++) {            String str = (String) combo.getItemAt(i);            if (str.equalsIgnoreCase(key))                return i;        }        return -1;    }    private void setFields(JComboBox combo, String[] fields, int index,            String first_entry)    {        combo.setEnabled(false);        combo.removeAllItems();        if (first_entry != null)            combo.addItem(first_entry);        for (int i = 0; i < fields.length; i++) {            String name = fields[i];            if (name.length() > 0) {                combo.addItem(name);            }        }        combo.setSelectedIndex(index);        combo.setEnabled(true);    }    private int getSelectionIndex(JComboBox combo)    {        int index = combo.getSelectedIndex();        if (index < 0) {            index = matchSelection(combo);            if (index < 0) {                if (combo == city) {                    if (parseLongLatText(combo, true)) {                        if (matchCity()) {                            return combo.getSelectedIndex();                        } else {                            return -1;                        }                    }                }                index = 0;            }            combo.setSelectedIndex(index);            updateFields();            return index;        } else {            return index;        }    }    private boolean parseLongLatText(JComboBox combo, boolean set)    {        double[] long_lat = new double[2];        if (City.parseLongLatitude((String) combo.getSelectedItem(), long_lat)) {            if (set) {                longitude = long_lat[0];                latitude = long_lat[1];                combo                        .setSelectedItem(City.formatLongLatitude(longitude,                                true, true, false)                                + ", "                                + City.formatLongLatitude(latitude, false,                                        true, false));            }            return true;        }        return false;    }    private void updateFields()    {        if (!country_selected.equals(getCountryName())) {            country_selected = getCountryName();            setFields(city, City.getCityList(country_selected), 0, null);            city_selected = getCityName();            setZone(country_selected, city_selected);        } else if (!city_matching && !city_selected.equals(getCityName())) {            city_selected = getCityName();            setZone(country_selected, city_selected);        }    }    private void setZone(String country_name, String city_name)    {        if (country_name == null || city_name == null)            return;        City c = City.matchCity(getCityName(), getCountryName(), false);        if (c != null) {            int z_len = zone.getItemCount();            for (int j = 0; j < z_len; j++) {                String z_name = (String) zone.getItemAt(j);                if (z_name.equals(c.getZoneName())) {                    zone.setSelectedIndex(j);                    break;                }            }        }    }    public void actionPerformed(ActionEvent event)    {        Object source = event.getSource();        if (source == country || source == city) {            if (((JComboBox) source).isEnabled())                updateFields();        }    }}