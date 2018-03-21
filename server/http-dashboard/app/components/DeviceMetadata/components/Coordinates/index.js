import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import CoordinatesModal from './modal';

class Coordinates extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
        {field.lat || <i>No Value</i>}, {field.lon || <i>No Value</i>}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <CoordinatesModal form={this.props.form}/>
      </div>
    );
  }

}

export default Coordinates;
