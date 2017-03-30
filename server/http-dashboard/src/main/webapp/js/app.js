import React from 'react';
import ReactDOM from 'react-dom';
import { HashRouter as Router, Link } from 'react-router-dom'
import { Route, Redirect } from 'react-router'
import Login from './pages/Login';
import Profile from './pages/Profile';
import { Layout } from 'antd';

const loggedIn = true;

ReactDOM.render(<Router>
    <div style={{height: '100%'}}>
        <Route exact path="/" render={() => (
            loggedIn ? <Redirect to="/login"/> : <Redirect to="/profile"/>
        )}/>
        <Route path="/login" component={Login}/>
        <Route path="/profile" component={Profile}/>
    </div>
</Router>, document.getElementById('app'));