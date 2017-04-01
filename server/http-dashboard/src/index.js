import React from 'react';
import ReactDOM from 'react-dom';
import {Router, Route, hashHistory, Redirect} from 'react-router';
import Layout from './components/Layout';

/* scenes */
import Login from './scenes/Login';

/* store */
import {Provider} from 'react-redux';
import Store from './store';

/* services */
import {RouteAuthorizedOnly, RouteGuestOnly} from 'services/Login';

Store().then((store) => {

  ReactDOM.render(
    <Provider store={store}>
      <Router history={hashHistory}>
        <Route component={Layout}>
          <Route onEnter={RouteAuthorizedOnly(store)}>

          </Route>
          <Route onEnter={RouteGuestOnly(store)}>
            <Route path="/login" component={Login}/>
          </Route>
        </Route>
        <Redirect from="*" to="/login"/>
      </Router>
    </Provider>,
    document.getElementById('app')
  );

});
