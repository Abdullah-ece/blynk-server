import React from 'react';
import {Form as BaseForm} from 'antd';
import {Field as ReduxField} from 'redux-form';
import classNames from 'classnames';
import './styles.less';

export default class Item extends React.Component {

  static propTypes = {
    children: React.PropTypes.any,
    label: React.PropTypes.string,
    className: React.PropTypes.string,
    type: React.PropTypes.string,
    position: React.PropTypes.string,
    offset: React.PropTypes.any
  };

  POSITION = {
    CENTER: 'center',
    TOP: 'top'
  };

  baseItem() {

    const className = classNames({
      [this.props.className]: true,
      'position-center': this.POSITION.CENTER === this.props.position,
      'position-top': this.POSITION.TOP === this.props.position,
      'none-offset': !this.props.offset,
      [`${this.props.offset}-offset`]: !!this.props.offset
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

    const className = classNames({
      [props.className]: true,
      'none-offset': !props.offset,
      [`${props.offset}-offset`]: !!props.offset
    });

    return (
      <BaseForm.Item
        className={className}
        label={props.label}
        validateStatus={validateStatus()}
        help={help()}>
        { React.cloneElement(props.element, props) }
      </BaseForm.Item>
    );
  }

  render() {

    const hasName = (element) => {
      if (Array.isArray(element)) {
        return element.some(hasName);
      } else {
        return element && element.props && element.props.name !== undefined;
      }
    };

    const element = this.props.children;

    if (hasName(element)) {
      return (
        <ReduxField {...element.props}
                    element={element}
                    label={this.props.label}
                    offset={this.props.offset}
                    component={this.reduxItem}/>
      );
    } else {
      return this.baseItem();
    }
  }
}
