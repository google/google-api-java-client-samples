// Copyright 2011 Google Inc. All Rights Reserved.

/**
 * @fileoverview This script posts to the data servlet with a request for data
 * to display until either the servlet responds with data or responds that it
 * failed. The servlet responds to each post with a message, which the script
 * displays to the user and data if it exists, which the script draws as a
 * motion chart.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */

// Load the motion chart package from the Visualization API and JQuery.
google.load('visualization', '1', {packages: ['motionchart']});
google.load('jquery', '1.6.4');

// Set a callback to run when the Google Visualization API is loaded.
google.setOnLoadCallback(doOnLoad);

function doOnLoad() {
  $('#query').hide();
  $('#toggle').click(function() {
    $('#query').toggle();
  });

  $('#refresh').click(function() {
    $('#refresh').attr('disabled', 'disabled');
    $('#message').html('Requesting that the query be rerun...');
    $.post('/', function() {
      setTimeout(postCheck, 2000);
    });
  });

  postCheck();
}

function postCheck() {
  $.post('/data', function(dataObject) {
    $('#message').html(dataObject.message);

    if (!dataObject.data && !dataObject.failed) {
      setTimeout(postCheck, 2000);
    } else {
      $('#refresh').removeAttr('disabled');
      if (dataObject.data) {
        $('#lastRun').html(dataObject.lastRun);
        
        var width = 800;
        var height = 400;
        var viz = $('#visualization');
        viz.css('width', width);
        viz.css('height', height);

        var dataTable = new google.visualization.DataTable(dataObject.data);
        var motionchart = new google.visualization.MotionChart(viz[0]);
        motionchart.draw(dataTable, {width: width, height: height});
      }
    }
  }, 'json');
}
