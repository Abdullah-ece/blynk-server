import React from 'react';
import ReactDOM from 'react-dom';
import {Router, Route, Redirect, useRouterHistory} from 'react-router';
import createBrowserHistory from 'history/lib/createBrowserHistory';
import Scroll from 'react-scroll';

import './canvasjs';

/* components */
import Layout, {UserLayout} from './components/Layout';
import LoginLayout from './components/LoginLayout';

/* scenes */
import Login from './scenes/Login';
import Book from './scenes/Book';
import Devices from './scenes/Devices';
import ForgotPass from './scenes/ForgotPass';
import ResetPass from './scenes/ResetPass';
import Logout from './scenes/Logout';
import StyleGuide from './scenes/StyleGuide';
import * as Organizations from './scenes/Organizations';
import Invite from './scenes/Invite';
import {ProductsIndex, ProductCreate, ProductDetails, ProductEdit, ProductClone} from './scenes/Products';
import UserProfile from './scenes/UserProfile';

/* store */
import {Provider} from 'react-redux';
import Store from './store';

/* services */
import {RouteGuestOnly, RouteAuthorizedOnly} from './services/Login';
import {
  Products as ProductsPreloader,
  Organization as OrganizationPreloader,
  Devices as DevicesPreloader
} from './services/Preloaders';

/* vendor */
import {LocaleProvider} from 'antd';
import enUS from 'antd/lib/locale-provider/en_US';

Store().then((store) => {

  const history = useRouterHistory(createBrowserHistory)({
    basename: '/dashboard'
  });

  history.listen(location => {

    setTimeout(() => {
      if (location.action === 'POP') {
        return;
      }
      Scroll.animateScroll.scrollToTop({
        duration: 250
      });
    });
  });

  ReactDOM.render(
    <Provider store={store}>
      <LocaleProvider locale={enUS}>
        <Router history={history}>
          <Route component={Book}>
            <Route path="/book" component={Book.Index}/>
            <Route path="/book/fieldset" component={Book.Fieldset}/>
            <Route path="/book/device-status" component={Book.DeviceStatus}/>
            <Route path="/book/device-auth-token" component={Book.DeviceAuthToken}/>
            <Route path="/book/content-editable" component={Book.ContentEditable}/>
            <Route path="/book/section" component={Book.Section}/>
            <Route path="/book/modal" component={Book.Modal}/>
            <Route path="/book/back-top" component={Book.BackTop}/>
            <Route path="/book/chart" component={Book.Chart}/>
            <Route path="/book/canvasjs" component={Book.Canvasjs}/>
          </Route>
          <Route component={Layout}>
            <Route component={UserLayout} onEnter={RouteAuthorizedOnly(store)}>
              <Route path="/user-profile" component={UserProfile} />
              <Route path="/user-profile/:tab" component={UserProfile} />
              <Route onEnter={DevicesPreloader(store)}>
                <Route path="/devices" components={Devices} onEnter={DevicesPreloader(store)}/>
                <Route path="/devices/create" components={Devices}/>
                <Route path="/devices/:id" components={Devices}/>
                <Route path="/devices/:id/create" components={Devices}/>
              </Route>
              <Route path="/organizations" component={Organizations.Index}/>
              <Route path="/organizations/create" component={Organizations.Create}/>
              <Route path="/organizations/edit/:id" component={Organizations.Edit}/>
              <Route path="/organizations/:id" component={Organizations.Details}/>
              <Route path="/products" component={ProductsIndex} onEnter={ProductsPreloader(store)}/>
              <Route path="/products/create" component={ProductCreate} onEnter={OrganizationPreloader(store)}/>
              <Route path="/products/create/:tab" component={ProductCreate}/>
              <Route path="/products/edit/:id" component={ProductEdit} onEnter={ProductsPreloader(store)}/>
              <Route path="/products/edit/:id/:tab" component={ProductEdit} onEnter={ProductsPreloader(store)}/>
              <Route path="/products/clone/:id" component={ProductClone} onEnter={ProductsPreloader(store)}/>
              <Route path="/products/clone/:id/:tab" component={ProductClone} onEnter={ProductsPreloader(store)}/>
              <Route path="/product/:id" component={ProductDetails} onEnter={ProductsPreloader(store)}/>
              <Route path="/product/:id/:tab" component={ProductDetails} onEnter={ProductsPreloader(store)}/>
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
