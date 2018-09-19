import React from 'react';
import Base from '../Base';
import {Fieldset, LinearIcon} from 'components';
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
        <Fieldset.Legend type="dark"> <LinearIcon type={field.icon || 'cube'}/> {field.name}         </Fieldset.Legend>
        {TimeService.fromTimestamp(field.time)}
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
