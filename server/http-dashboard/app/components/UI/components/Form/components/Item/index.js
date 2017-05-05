import React from 'react';
import {Form as BaseForm} from 'antd';
import {Field as ReduxField} from 'redux-form';
import classNames from 'classnames';

export default class Item extends React.Component {

  static propTypes = {
    children: React.PropTypes.object,
    label: React.PropTypes.string,
    className: React.PropTypes.string,
    type: React.PropTypes.string,
    position: React.PropTypes.string
  };

  POSITION = {
    CENTER: 'center',
    TOP: 'top'
  };

  baseItem() {

    const className = classNames({
      [this.props.className]: true,
      'position-center': this.POSITION.CENTER === this.props.position,
      'position-top': this.POSITION.TOP === this.props.position
    });

    return (
      <BaseForm.Item
        className={className}>
        { this.props.children }
      </BaseForm.Item>
    );
  }

  reduxItem(props) {
    const {displayError = true, meta: {touched, error, warning}} = props;

    const validateStatus = () => {
      return touched && displayError ? (error ? 'error' : warning ? 'warning' : '' ) : 'success';
    };

    const help = () => {
      return touched && displayError ? (error || warning ? error || warning : '' ) : '';
    };

    return (
      <BaseForm.Item
        className={props.className}
        label={props.label}
        validateStatus={validateStatus()}
        help={help()}>
        { React.cloneElement(props.element, props) }
      </BaseForm.Item>
    );
  }

  render() {
    const element = this.props.children;

    if (element.props.name) {
      return (
        <ReduxField {...element.props}
                    element={element}
                    label={this.props.label}
                    component={this.reduxItem}/>
      );
    } else {
      return this.baseItem();
    }
  }
}
