import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import CostModal from './modal';
import {Currency, Unit} from 'services/Products';

class Cost extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    const perValue = field.get('perValue');
    const price = field.get('price');
    const units = field.get('units');
    const currency = field.get('currency');

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        { !price && !units ? <i>No Value</i> : (
          <div>
            { Number(field.get('perValue')) === 1 ? (
              <p>{`${Currency[currency].abbreviation} ${price} / ${Unit[units].abbreviation}`}</p>
            ) : (
              <p>{`${Currency[currency].abbreviation} ${price} / ${perValue} ${Unit[units].abbreviation}`}</p>
            )}
          </div>
        ) }
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <CostModal form={this.props.form}/>
      </div>
    );
  }

}

export default Cost;
