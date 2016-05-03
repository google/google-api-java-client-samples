/*
 * Copyright 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.samples.cloudnotes;

import com.appspot.api.services.noteendpoint.model.Note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Sriram Saroop
 */
class NoteAdapter extends BaseAdapter {

  final static class ViewHolder {
    TextView title;
  }

  private Comparator<Note> taskComparator = new Comparator<Note>() {
    public int compare(Note object1, Note object2) {
      return object1.getDescription().compareTo(object2.getDescription());
    }
  };

  private final List<Note> items = new ArrayList<Note>();
  private final LayoutInflater inflater;

  NoteAdapter(Context context) {
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  void setTasks(List<Note> tasks) {
    synchronized (this.items) {
      items.clear();
      if (tasks != null) {
        items.addAll(tasks);
        Collections.sort(items, taskComparator);
      }
    }
  }

  void removeTask(String id) {
    synchronized (items) {
      for (Iterator<Note> it = items.iterator(); it.hasNext();) {
        if (id.equalsIgnoreCase(it.next().getId())) {
          it.remove();
          break;
        }
      }
      Collections.sort(items, taskComparator);
    }
  }

  void addTask(Note item) {
    synchronized (items) {
      boolean found = false;
      for (ListIterator<Note> it = items.listIterator(); it.hasNext();) {
        if (it.next().getId().equals(item.getId())) {
          it.set(item);
          found = true;
          break;
        }
      }
      if (!found) {
        items.add(item);
      }
      Collections.sort(items, taskComparator);
    }
  }

  public int getCount() {
    synchronized (items) {
      return items.size();
    }
  }

  public Note getItem(int position) {
    synchronized (items) {
      return items.get(position);
    }
  }

  public long getItemId(int position) {
    return position;
  }

  public View getView(int position, View convertView, ViewGroup view) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.listitem, null);
      holder = new ViewHolder();
      holder.title = (TextView) convertView.findViewById(R.id.taskTitle);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    Note task = getItem(position);
    holder.title.setText(task.getDescription());
    return convertView;
  }
}
