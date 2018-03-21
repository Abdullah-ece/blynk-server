import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import NumberModal from './modal';

class Number extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
        {field.value}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <NumberModal form={this.props.form}/>
      </div>
    );
  }

}

export default Number;
