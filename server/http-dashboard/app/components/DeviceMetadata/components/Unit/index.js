import React from 'react';
import Base from '../Base';
import {Fieldset, LinearIcon} from 'components';
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
        <Fieldset.Legend type="dark"> <LinearIcon type={field.icon || 'cube'}/> {field.name}         </Fieldset.Legend>
        {!field.value && !field.units ? <i>No Value</i> : (
          <div>
            {field.value} {Units[field.units].abbreviation}
          </div>
        )}
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
