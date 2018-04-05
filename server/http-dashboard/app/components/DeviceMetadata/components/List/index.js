import React from 'react';
import Base from '../Base';
import {Fieldset} from 'components';
import ListModal from './modal';

class List extends Base {

  constructor(props) {
    super(props);
  }

  getPreviewComponent() {

    const field = this.props.data;

    const value = field.selectedOption;

    return (
      <Fieldset>
        <Fieldset.Legend type="dark">{field.name}</Fieldset.Legend>
        { value || (<i>No Value</i>)}
      </Fieldset>
    );
  }

  getEditableComponent() {
    let field = this.props.data;

    let options = (Array.isArray(field.options) ? field.options : []).map((option) => ({
      key: option,
      value: option
    }));

    return (
      <div>
        <ListModal form={this.props.form} options={options}/>
      </div>
    );
  }

}

export default List;
