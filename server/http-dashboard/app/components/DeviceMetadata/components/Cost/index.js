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

    const perValue = field.perValue;
    const price = field.price;
    const units = field.units;
    const currency = field.currency;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
        { !price && !units ? <i>No Value</i> : (
          <div>
            { Number(field.perValue) === 1 ? (
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
