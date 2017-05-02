import React from 'react';
import {AddDataStreamsFields} from 'scenes/Products/components/AddField';
import {DataStreamsBaseField, DataStreamsItemsList} from "scenes/Products/components/DataStreams";
import {SortableContainer, SortableElement, arrayMove} from 'react-sortable-hoc';
import _ from 'lodash';

class DataStreams extends React.Component {

  static propTypes = {
    fields: React.PropTypes.array,
    onFieldChange: React.PropTypes.func,
    onFieldsChange: React.PropTypes.func,
  };

  SortableItem = SortableElement(({value}) => {

    const field = value;

    const props = {
      id: field.id,
      key: field.id,
      form: `datastreamfield${field.id}`,
      onChange: this.handleChangeField.bind(this),
      validate: this.fieldsValidation.bind(this),
      onDelete: this.handleDeleteField.bind(this),
      onClone: this.handleCloneField.bind(this),
      initialValues: {
        name: field.values.name,
        units: field.values.units,
        min: field.values.min,
        max: field.values.max,
        pin: field.values.pin
      }
    };

    return (
      <DataStreamsBaseField
        {...props}
      />
    );


  });

  SortableList = SortableContainer(({items}) => {
    return (
      <DataStreamsItemsList>
        {items.map((value, index) => {
          return (
            <this.SortableItem key={`item-${value.id}`} index={index} value={value}/>
          );
        })}
      </DataStreamsItemsList>
    );
  });

  handleChangeField(values, dispatch, props) {
    const field = _.find(this.props.fields, {id: props.id});

    this.props.onFieldChange({
      ...field,
      values
    });
  }

  fieldsValidation() {
    console.log(`validate for uniq pins & names${this.props.id}`);
  }

  handleDeleteField(key) {
    this.props.onFieldsChange(this.props.fields.filter((field) => field.id !== key));
  }

  handleCloneField(id) {

    const cloned = _.find(this.props.fields, {id: id});

    const nextId = this.props.fields.reduce((acc, value) => (
        acc < value.id ? value.id : acc
      ), this.props.fields.length ? this.props.fields[0].id : 0) + 1;

    const fields = [
      ...this.props.fields,
      {
        ...cloned,
        id: nextId,
        values: {
          ...cloned.values,
          name: `${cloned.values.name} Copy`,
          pin: ''
        }
      }
    ];

    const originalIndex = _.findIndex(fields, {id: id});

    this.props.onFieldsChange(
      arrayMove(fields, fields.length - 1, originalIndex + 1)
    );
  }

  addDataStreamsField() {
    //  some there
  }

  onSortEnd({oldIndex, newIndex}) {

    this.props.onFieldsChange(
      arrayMove(this.props.fields, oldIndex, newIndex)
    );

  }

  render() {

    return (
      <div>
        { this.props.fields && this.props.fields.length && (
          <this.SortableList items={this.props.fields} onSortEnd={this.onSortEnd.bind(this)}
                             useDragHandle={true}
                             lockAxis="y"
                             helperClass="product-item-drag-active"/>) || null
        }
        {/*<DataStreamsItemsList>*/}
        {/*<DataStreamsBaseField {...props}/>*/}
        {/*</DataStreamsItemsList>*/}
        <AddDataStreamsFields onFieldAdd={this.addDataStreamsField.bind(this)}/>
      </div>
    );
  }
}

export default DataStreams;
