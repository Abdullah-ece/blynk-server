import React from 'react';
import Base from '../Base';
import {Fieldset, LinearIcon} from 'components';
import SwitchModal from './modal';

class Switch extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark"> <LinearIcon type={field.icon || 'cube'}/> {field.name}         </Fieldset.Legend>
        {field.value ? Number(field.value) === 0 ? field.from : field.to : 'Not selected'}
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
