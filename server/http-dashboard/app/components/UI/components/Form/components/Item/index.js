import React from 'react';
import {Form as BaseForm} from 'antd';
import {Field as ReduxField} from 'redux-form';

export default class Item extends React.Component {

  static propTypes = {
    children: React.PropTypes.object
  };

  field(props) {
    return React.cloneElement(props.element, props);
  }

  render() {
    const props = this.props;

    let element = props.children;

    return (
      <BaseForm.Item>
        {element && (
          <ReduxField {...element.props} element={element} component={ this.field }/>
        )}
      </BaseForm.Item>
    );
  }
}
