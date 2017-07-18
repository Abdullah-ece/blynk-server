import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import UnitModal from './modal';
import {Unit as Units} from 'services/Products';

class Unit extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        { !field.get('value') && !field.get('units') ? <i>No Value</i> : (
          <div>
            {field.get('value')} {Units[field.get('units')].abbreviation}
          </div>
        ) }
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <UnitModal form={this.props.form}/>
      </div>
    );
  }

}

export default Unit;
