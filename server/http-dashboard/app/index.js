import React from 'react';
import ReactDOM from 'react-dom';
import {Router, Route, hashHistory, Redirect} from 'react-router';

/* components */
import Layout, {UserLayout, UserProfileLayout} from './components/Layout';
import LoginLayout from './components/LoginLayout';

/* scenes */
import Login from './scenes/Login';
import ForgotPass from './scenes/ForgotPass';
import ResetPass from './scenes/ResetPass';
import Logout from './scenes/Logout';
import StyleGuide from './scenes/StyleGuide';
import Invite from './scenes/Invite';
import {MyAccount, OrganizationSettings} from './scenes/UserProfile';
import {ProductsIndex, ProductCreate} from './scenes/Products';

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
              <Route component={UserProfileLayout}>
                <Route path="/account" component={MyAccount}/>
                <Route path="/organization-settings" component={OrganizationSettings}/>
              </Route>
              <Route path="/products" component={ProductsIndex}/>
              <Route path="/products/create" component={ProductCreate}/>
              <Route path="/products/create/:tab" component={ProductCreate}/>
            </Route>
            <Route path="/logout" component={Logout}/>
            <Route component={LoginLayout}>
              <Route path="/login" component={Login} onEnter={RouteGuestOnly(store)}/>
              <Route path="/forgot-pass" component={ForgotPass} onEnter={RouteGuestOnly(store)}/>
              <Route path="/resetpass" component={ResetPass}/>
              <Route path="/invite" component={Invite}/>
            </Route>
          </Route>
          <Route component={StyleGuide} path="/style-guide"/>
          <Redirect from="*" to="/login"/>
        </Router>
      </LocaleProvider>
    </Provider>,
    document.getElementById('app')
  );

});
