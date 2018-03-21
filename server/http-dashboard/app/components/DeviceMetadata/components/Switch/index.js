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
        <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
        {Number(field.value) === 0 ? field.from : field.to}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <SwitchModal initialValues={this.props.data}/>
      </div>
    );
  }

}

export default Switch;
