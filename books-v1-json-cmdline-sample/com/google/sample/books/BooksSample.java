/*
 * Copyright (c) 2011 Google Inc.
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

package com.google.sample.books;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.books.v1.Books;
import com.google.api.services.books.v1.Books.Volumes.List;
import com.google.api.services.books.v1.model.Volume;
import com.google.api.services.books.v1.model.VolumeSaleInfo;
import com.google.api.services.books.v1.model.VolumeVolumeInfo;
import com.google.api.services.books.v1.model.Volumes;

import java.net.URLEncoder;
import java.text.NumberFormat;

/**
 * A sample application that demonstrates how Goole Books Client Library for
 * Java can be used to query Google Books. It accepts queries in the command
 * line, and prints the results to the console.
 *
 * $ java com.google.sample.books.BooksSample [--author|--isbn|--title] "<query>"
 *
 * Please start by reviewing the Google Books API documentation at:
 * http://code.google.com/apis/books/docs/getting_started.html
 */
public class BooksSample {

  // Sign up for your API Key at https://code.google.com/apis/console
  // Only limited requests can be made without an API key for development.
  // Production apps should use an API key to for higher production quota.
  // You can also request for even higher quota, but only with an API Key.
  private static final String API_KEY = null;

  private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance();
  private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();

  private static void queryGoogleBooks(JsonFactory jsonFactory, String query) throws Exception {
    // Set up Books client.
    final Books books = new Books(new NetHttpTransport(), jsonFactory);
    books.setApplicationName("Google-BooksSample/1.0");
    if (API_KEY != null && API_KEY.length() > 0) {
      books.accessKey = API_KEY;
    }

    // Set query string and filter only Google eBooks.
    System.out.println("Query: [" + query + "]");
    List volumesList = books.volumes.list(query);
    volumesList.filter = "ebooks";

    // Execute the query.
    Volumes volumes = volumesList.execute();
    if (volumes.totalItems == 0 || volumes.items == null) {
      System.out.println("No matches found.");
      return;
    }

    // Output results.
    for (Volume volume : volumes.items) {
      VolumeVolumeInfo volumeInfo = volume.volumeInfo;
      VolumeSaleInfo saleInfo = volume.saleInfo;
      System.out.println("==========");
      // Title.
      System.out.println("Title: " + volumeInfo.title);
      // Author(s).
      java.util.List<String> authors = volumeInfo.authors;
      if (authors != null && !authors.isEmpty()) {
        System.out.print("Author(s): ");
        for (int i = 0; i < authors.size(); ++i) {
          System.out.print(authors.get(i));
          if (i < authors.size() - 1) {
            System.out.print(", ");
          }
        }
        System.out.println();
      }
      // Description (if any).
      if (volumeInfo.description != null && volumeInfo.description.length() > 0) {
        System.out.println("Description: " + volumeInfo.description);
      }
      // Ratings (if any).
      if (volumeInfo.ratingsCount != null && volumeInfo.ratingsCount > 0) {
        int fullRating = (int) Math.round(volumeInfo.averageRating.doubleValue());
        System.out.print("User Rating: ");
        for (int i = 0; i < fullRating; ++i) {
          System.out.print("*");
        }
        System.out.println(" (" + volumeInfo.ratingsCount + " rating(s))");
      }
      // Price (if any).
      if ("FOR_SALE".equals(saleInfo.saleability)) {
        double save = saleInfo.listPrice.amount - saleInfo.retailPrice.amount;
        if (save > 0.0) {
          System.out.print("List: " + CURRENCY_FORMATTER.format(saleInfo.listPrice.amount)
              + "  ");
        }
        System.out.print("Google eBooks Price: "
            + CURRENCY_FORMATTER.format(saleInfo.retailPrice.amount));
        if (save > 0.0) {
          System.out.print("  You Save: " + CURRENCY_FORMATTER.format(save) + " ("
              + PERCENT_FORMATTER.format(save / saleInfo.listPrice.amount) + ")");
        }
        System.out.println();
      }
      // Access status.
      String accessViewStatus = volume.accessInfo.accessViewStatus;
      String message = "Additional information about this book is available from Google eBooks at:";
      if ("FULL_PUBLIC_DOMAIN".equals(accessViewStatus)) {
        message = "This public domain book is available for free from Google eBooks at:";
      } else if ("SAMPLE".equals(accessViewStatus)) {
        message = "A preview of this book is available from Google eBooks at:";
      }
      System.out.println(message);
      // Link to Google eBooks.
      System.out.println(volumeInfo.infoLink);
    }
    System.out.println("==========");
    System.out.println(volumes.totalItems + " total results at http://books.google.com/ebooks?q="
        + URLEncoder.encode(query, "UTF-8"));
  }

  public static void main(String[] args) {
    JsonFactory jsonFactory = new JacksonFactory();
    try {
      // Verify command line parameters.
      if (args.length == 0) {
        System.err.println("Usage: BooksSample [--author|--isbn|--title] \"<query>\"");
        System.exit(1);
      }
      // Parse command line parameters into a query.
      // Query format: "[<author|isbn|intitle>:]<query>"
      String prefix = null;
      String query = "";
      for (String arg : args) {
        if ("--author".equals(arg)) {
          prefix = "inauthor:";
        } else if ("--isbn".equals(arg)) {
          prefix = "isbn:";
        } else if ("--title".equals(arg)) {
          prefix = "intitle:";
        } else if (arg.startsWith("--")) {
          System.err.println("Unknown argument: " + arg);
          System.exit(1);
        } else {
          query = arg;
        }
      }
      if (prefix != null) {
        query = prefix + query;
      }
      try {
        queryGoogleBooks(jsonFactory, query);
        // Success!
        return;
      } catch (HttpResponseException e) {
        if (!e.response.contentType.equals(Json.CONTENT_TYPE)) {
          System.err.println(e.response.parseAsString());
        } else {
          GoogleJsonError errorResponse = GoogleJsonError.parse(jsonFactory, e.response);
          System.err.println(errorResponse.code + " Error: " + errorResponse.message);
          for (ErrorInfo error : errorResponse.errors) {
            System.err.println(jsonFactory.toString(error));
          }
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(0);
  }
}
