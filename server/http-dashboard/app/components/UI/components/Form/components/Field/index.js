import React from 'react';
// import FormItem from 'components/FormItem';
// import {Form, Input, Icon} from 'antd';
//
// import {Field as FormField} from 'redux-form';
//
export default class Field extends React.Component {
//
//   static propTypes = {
//     title: React.PropTypes.string
//   };
//
//   titledField({title, displayError = true, placeholder, rows, input, type, icon, meta: {touched, error, warning}}) {
//     return (
//       <Form.Item validateStatus={touched && displayError ? (error ? 'error' : warning ? 'warning' : '' ) : 'success'}
//                  className="form-field"
//                  help={touched && displayError ? (error || warning ? error || warning : '' ) : ''}>
//         <FormItem>
//           <FormItem.Title>
//             { title }
//           </FormItem.Title>
//           <FormItem.Content>
//             <Input {...input} rows={rows} type={type} placeholder={placeholder}
//                    prefix={icon ? <Icon type={icon} className="form--field-icon"/> : null}/>
//           </FormItem.Content>
//         </FormItem>
//       </Form.Item>
//     );
//   }
//
//   simpleField({displayError = true, placeholder, rows, input, type, icon, meta: {touched, error, warning}}) {
//     return (
//       <Form.Item validateStatus={touched && displayError ? (error ? 'error' : warning ? 'warning' : '' ) : 'success'}
//                  className="form-field"
//                  help={touched && displayError ? (error || warning ? error || warning : '' ) : ''}>
//         <Input {...input} rows={rows} type={type} placeholder={placeholder}
//                prefix={icon ? <Icon type={icon} className="form--field-icon"/> : null}/>
//       </Form.Item>
//     );
//   }
//
//
//   render() {
//     const props = this.props;
//
//     if (this.props.title) {
//       return (
//         <FormField {...props} component={this.titledField}/>
//       );
//     } else {
//       return (
//         <FormField {...props} component={this.simpleField}/>
//       );
//     }
//   }
}
