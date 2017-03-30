import React from 'react';
import axios from 'axios';
import createHistory from 'history/createHashHistory'
import CryptoJS from 'crypto-js';
import {Row, Col, Form, Icon, Input, Button, Checkbox} from 'antd';
import styles from '../../less/login.less';

const FormItem = Form.Item;
const history = createHistory();

//this is all work in progress, this will move into a separate file, router will be created

function hasErrors(fieldsError) {
    return Object.keys(fieldsError).some(field => fieldsError[field]);
}

class Login extends React.Component {
    componentDidMount() {
        this.props.form.validateFields();
    }

    handleSubmit(e) {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const email = values.email;
                const algo = CryptoJS.algo.SHA256.create();
                algo.update(values.password, 'utf-8');
                algo.update(CryptoJS.SHA256(email.toLowerCase()), 'utf-8');
                const password = algo.finalize().toString(CryptoJS.enc.Base64);

                const params = new URLSearchParams();
                params.append('email', email);
                params.append('password', password);
                axios.post('/dashboard/login', params)
                    .then(function (response) {
                        console.log(response);
                        history.push('/profile');
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        });
    }

    render() {
        const { getFieldDecorator, getFieldsError, getFieldError, isFieldTouched } = this.props.form;

        const userNameError = isFieldTouched('userName') && getFieldError('userName');
        const passwordError = isFieldTouched('password') && getFieldError('password');

        return (<Row type="flex" justify="space-around" align="middle" style={{height: '100%'}}>
                <Col>
                    <Form onSubmit={this.handleSubmit.bind(this)} className="login-form">
                        <FormItem>
                            <span className="form-header">Log in to Blynk</span>
                        </FormItem>
                        <FormItem validateStatus={userNameError ? 'error' : ''}
                                  help={userNameError || ''}>
                            {getFieldDecorator('email', {
                                rules: [{
                                    type: 'email', message: 'The input is not valid E-mail!',
                                }, {
                                    required: true, message: 'Please input your E-mail!',
                                }],
                            })(
                                <Input prefix={<Icon type="user" style={{fontSize: 13}}/>} placeholder="email"/>
                            )}
                        </FormItem>
                        <FormItem validateStatus={passwordError ? 'error' : ''}
                                  help={passwordError || ''}>
                            {getFieldDecorator('password', {
                                rules: [{required: true, message: 'Please input your Password!'}],
                            })(
                                <Input prefix={<Icon type="lock" style={{fontSize: 13}}/>} type="password"
                                       placeholder="password"/>
                            )}
                        </FormItem>
                        <FormItem>
                            <Button type="primary" htmlType="submit" className="login-form-button"
                                    disabled={hasErrors(getFieldsError())}>
                                Log in
                            </Button>

                        </FormItem>
                        <FormItem>
                            <a className="login-form-forgot">Forgot password?</a>
                        </FormItem>
                    </Form>
                </Col>
            </Row>
        );
    }
}

export default Form.create()(Login);
