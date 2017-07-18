import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import SwitchModal from './modal';

class Switch extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        {Number(field.get('value')) === 0 ? field.get('from') : field.get('to')}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <SwitchModal initialValues={this.props.data.toJS()}/>
      </div>
    );
  }

}

export default Switch;
