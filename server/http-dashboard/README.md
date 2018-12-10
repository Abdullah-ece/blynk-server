# some Dashboard

### Configuration options
Through environment variables you can change behavior of the application functionality. Available options:
1. BLYNK_WATERMARK - if set to true, it will add a watermark to the bottom right screen of the dashboard. It will show current build date, last commit hash and commit date
2. BLYNK_POWERED_BY - if set to true, it will show powered by Blynk footer on the login screen
3. BLYNK_ANALYTICS - if set to true, it will show analytics tab inside dashboard menu

### Dependencies
**Global Dependencies**: Git, NPM, NodeJS v6
**NPM Dependencies**: babel-cli

### How to start
1. Clone project
2. `$ npm  install` to install dependencies
3. `$ npm start -s` to start projct

### Available commands

`$ npm start -s` to start project and watch changes
`$ npm run build` to build `dist/` version
`$ npm run test` to run tests

### Application structure

There is example of project structure
```
src/
    components/
    data/
    scenes/
    services/
    store/
        reducers.js
        index.js
    index.js
```

**components** - Components defined at the root level of project, in the components folder, are global and can be used anywhere in  application. But if you decide to define a new component inside another component (nesting), this new component can only be used its direct parent.

**data** - A data entity is very similar to a service. You can see it as your bridge/an adapter between the server API and the client. It is in charge of most of the network calls your app will make, get and post content, and transform payloads as needed before being sent, or stored in the Redux Store. Components can define own data inside it but can only be used its direct parent. 
**scenes** - A scene is a page of application. Scene can define own subscenes or components but them can only be used are direct parent.

**sevices** - Helpers and reused code inside app. Services can't be components. Great example of Service is Fields Validation service which contains regexpes of fields.

**store** - You can't create anything here. There is root reducer and store initialization. On this folder you can import your own reducers to attach them to store or attach redux middlewares. 

Each **component**, **scene** may has own components, scenes, data, services but make sure them used only by parent. If any component or sub scene used on another component or scene (except parent) you should move it to global scope. 

Example of app structure is there: 

```
src/
    components/
        Navigation/
            components/
                Link/
                    index.js
                    styles.scss
            index.js
            styles.scss
    data/
        Users/
            actions.js
            reducers.js
    scenes/
        Home/
            index.js
            styles.scss
        Login/
            components/
                LoginForm/
                    index.js
            index.js
            actions.js
            reducers.js
            styles.scss
        Signup
            components/
                SignupForm/
                    index.js
                    
            index.js
            actions.js
            reducers.js
            styles.scss
    services/
        Validation/
            rules/
                somerule.js
                index.js
            index.js
    store/
        reducers.js
        index.js
    index.js
```

## The Bible of some Dashboard development

1. Components can not be large. Divide the huge components into small ones.
2. All resouble helpers are services and should be in the services folder.
3. All pages of the application are scenes and should be in the scene folder
4. All sub-scenes that belong to the same parent scene must be the parent sub-scenes and located in the corresponding directory (the components architecture is the same)
5. The name of the styles should correspond to the following structure:
```
component-style (Ex: login-header)
component--subcomponent-style (login--login-form-header)
scene--subcomponent-style (home--left-sidebar--menu)
```
6. **Don't PUT STYLES ON HTML**. Use scss classes for that instead. 
7. Webpack has integrated eslinter. **DON'T COMMIT if your eslint has warnings or errors** _@todo provide pre-commit feature which prevent commit if eslint has warnings_
8. For actions which make requests to API via axios-redux-middleware use `API_` prefix. _Ex: API_LOGIN, API_USERS_

@Todo: Split Bible for categories: API, app styling, Validation, Session, Testing
