import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import TimeModal from './modal';
import {Time as TimeService} from 'services/Metadata';

class Time extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.get('name')}</Fieldset.Legend>
        {TimeService.fromTimestamp(field.get('time'))}
      </Fieldset>
    );
  }

  getEditableComponent() {
    return (
      <div>
        <TimeModal form={this.props.form} initialValues={this.props.initialValues}/>
      </div>
    );
  }

}

export default Time;
