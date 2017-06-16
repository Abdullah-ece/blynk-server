import React from 'react';
import ReactDOM from 'react-dom';
import {Router, Route, hashHistory, Redirect} from 'react-router';

/* components */
import Layout, {UserLayout, UserProfileLayout} from './components/Layout';
import LoginLayout from './components/LoginLayout';

/* scenes */
import Login from './scenes/Login';
import Book from './scenes/Book';
import Devices from './scenes/Devices';
import ForgotPass from './scenes/ForgotPass';
import ResetPass from './scenes/ResetPass';
import Logout from './scenes/Logout';
import StyleGuide from './scenes/StyleGuide';
import Invite from './scenes/Invite';
import {MyAccount, OrganizationSettings} from './scenes/UserProfile';
import {ProductsIndex, ProductCreate, ProductDetails, ProductEdit, ProductClone} from './scenes/Products';

/* store */
import {Provider} from 'react-redux';
import Store from './store';

/* services */
import {RouteGuestOnly, RouteAuthorizedOnly} from './services/Login';
import {
  Products as ProductsPreloader,
  Account as AccountPreloader,
  Organization as OrganizationPreloader,
  Devices as DevicesPreloader,
  Device as DevicePreloader,
  DeviceCreate as DeviceCreatePreload,
  DeviceByIdCreate as DeviceByIdCreatePreload
} from './services/Preloaders';

/* vendor */
import {LocaleProvider} from 'antd';
import enUS from 'antd/lib/locale-provider/en_US';

Store().then((store) => {

  ReactDOM.render(
    <Provider store={store}>
      <LocaleProvider locale={enUS}>
        <Router history={hashHistory} onUpdate={() => window.scrollTo(0, 0)}>
          <Route component={Book}>
            <Route path="/book" component={Book.Index}/>
            <Route path="/book/fieldset" component={Book.Fieldset}/>
            <Route path="/book/device-status" component={Book.DeviceStatus}/>
            <Route path="/book/device-auth-token" component={Book.DeviceAuthToken}/>
            <Route path="/book/content-editable" component={Book.ContentEditable}/>
            <Route path="/book/section" component={Book.Section}/>
            <Route path="/book/modal" component={Book.Modal}/>
          </Route>
          <Route component={Layout}>
            <Route component={UserLayout} onEnter={RouteAuthorizedOnly(store)}>
              <Route component={UserProfileLayout}>
                <Route path="/account" component={MyAccount}
                       onEnter={AccountPreloader(store)}/>
                <Route path="/organization-settings" component={OrganizationSettings}
                       onEnter={OrganizationPreloader(store)}/>
              </Route>
              <Route onEnter={DevicesPreloader(store)}>
                <Route path="/devices" components={Devices} onEnter={DevicesPreloader(store)}/>
                <Route path="/devices/create" components={Devices} onEnter={DeviceCreatePreload(store)}/>
                <Route path="/devices/:id" components={Devices} onEnter={DevicePreloader(store)}/>
                <Route path="/devices/:id/create" components={Devices} onEnter={DeviceByIdCreatePreload(store)}/>
              </Route>
              <Route path="/products" component={ProductsIndex} onEnter={ProductsPreloader(store)}/>
              <Route path="/products/create" component={ProductCreate}/>
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
