package com.ifountain.es

import org.elasticsearch.action.ActionListener

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 26, 2010
 * Time: 6:20:52 PM
 */
class ClosureActionListener implements ActionListener {
  def closure;

  public ClosureActionListener(c) {
    closure = c;
  }

  public void onFailure(Throwable throwable) {
    closure(null, throwable);
  }

  public void onResponse(Object response) {
    closure(response, null);
  }
}
