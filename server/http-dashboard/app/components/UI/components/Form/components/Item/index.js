import React from 'react';
import {Form as BaseForm} from 'antd';
import {Field as ReduxField} from 'redux-form';

export default class Item extends React.Component {

  static propTypes = {
    children: React.PropTypes.object,
    label: React.PropTypes.string
  };

  item(props) {
    const {displayError = true, meta: {touched, error, warning}} = props;

    const validateStatus = () => {
      return touched && displayError ? (error ? 'error' : warning ? 'warning' : '' ) : 'success';
    };

    const help = () => {
      return touched && displayError ? (error || warning ? error || warning : '' ) : '';
    };

    return (
      <BaseForm.Item
        label={props.label}
        validateStatus={validateStatus()}
        help={help()}>
        { React.cloneElement(props.element, props) }
      </BaseForm.Item>
    );
  }

  render() {
    const element = this.props.children;
    return (
      <ReduxField {...element.props}
                  element={element}
                  label={this.props.label}
                  component={this.item}/>
    );
  }
}
