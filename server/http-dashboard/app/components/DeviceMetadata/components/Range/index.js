import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import RangeModal from './modal';
import {TimeRange} from 'services/Metadata';

class Range extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
        from {TimeRange.fromMinutes(field.from)} to {TimeRange.fromMinutes(field.to)}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <RangeModal form={this.props.form} initialValues={this.props.data}/>
      </div>
    );
  }

}

export default Range;
