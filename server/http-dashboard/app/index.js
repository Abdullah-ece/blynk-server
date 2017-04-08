import React from 'react';
import ReactDOM from 'react-dom';
import {Router, Route, hashHistory, Redirect} from 'react-router';

/* components */
import Layout from './components/Layout';
import UserLayout from './components/UserLayout';
import LoginLayout from './components/LoginLayout';

/* scenes */
import Login from './scenes/Login';
import ForgotPass from './scenes/ForgotPass';
import Logout from './scenes/Logout';
import {MyAccount, OrganizationSettings} from './scenes/UserProfile';

/* store */
import {Provider} from 'react-redux';
import Store from './store';

/* services */
import {RouteGuestOnly, RouteAuthorizedOnly} from './services/Login';

/* vendor */
import {LocaleProvider} from 'antd';
import enUS from 'antd/lib/locale-provider/en_US';

Store().then((store) => {

  ReactDOM.render(
    <Provider store={store}>
      <LocaleProvider locale={enUS}>
        <Router history={hashHistory}>
          <Route component={Layout}>
            <Route component={UserLayout} onEnter={RouteAuthorizedOnly(store)}>
              <Route path="/account" component={MyAccount}/>
              <Route path="/organization-settings" component={OrganizationSettings}/>
            </Route>
            <Route path="/logout" component={Logout}/>
            <Route component={LoginLayout} onEnter={RouteGuestOnly(store)}>
              <Route path="/login" component={Login}/>
              <Route path="/forgot-pass" component={ForgotPass}/>
            </Route>
          </Route>
          <Redirect from="*" to="/login"/>
        </Router>
      </LocaleProvider>
    </Provider>,
    document.getElementById('app')
  );

});
