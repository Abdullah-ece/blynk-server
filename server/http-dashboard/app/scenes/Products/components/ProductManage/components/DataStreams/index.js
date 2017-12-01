import React from 'react';
import Scroll from 'react-scroll';
import {BackTop} from 'components';
import {AddDataStreamsFields} from 'scenes/Products/components/AddField';
import {DataStreamsBaseField, DataStreamsItemsList} from "scenes/Products/components/DataStreams";
import {Unit} from "services/Products";
import {SortableContainer, SortableElement, arrayMove} from 'react-sortable-hoc';
import _ from 'lodash';
import classnames from 'classnames';

class DataStreams extends React.Component {

  static propTypes = {
    fields: React.PropTypes.array,
    onFieldChange: React.PropTypes.func,
    onFieldsChange: React.PropTypes.func,
  };

  state = {
    isSortEnabled: false
  };

  componentDidUpdate() {
    this.props.fields.forEach((field) => {
      if (field && field.values && field.values.isRecentlyCreated) {

        Scroll.scroller.scrollTo(`${field.name}`, {
          duration: 1000,
          offset: -64,
          smooth: "easeInOutQuint",
        });

        this.handleChangeField({
          ...field,
          values: {
            ...field.values,
            isRecentlyCreated: false
          }
        });
      }
    });
  }

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
      field: field,
      initialValues: {
        label: field.values.label,
        units: field.values.units,
        min: field.values.min,
        max: field.values.max,
        pin: String(field.values.pin),
      }
    };

    let element = (<DataStreamsBaseField {...props} />);

    return element;

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
    if (values.id) {
      // updates full entity
      this.props.onFieldChange(values);
    } else {
      // updates only values of entity
      const field = _.find(this.props.fields, {id: props.id});

      this.props.onFieldChange({
        ...field,
        values: {
          ...field.values,
          ...values,
        }
      });
    }
  }

  fieldsValidation(values, props) {
    const errors = {};

    this.props.fields.forEach((field) => {
      if (field.values.name === values.name && Number(props.id) !== Number(field.id)) {
        errors.name = 'Name should be unique';
      }
      if (Number(field.values.pin) === Number(values.pin) && Number(props.id) !== Number(field.id)) {
        errors.pin = 'Pin should be unique';
      }
    });

    return errors;
  }

  handleDeleteField(key) {
    this.props.onFieldsChange(this.props.fields.filter((field) => field.id !== key));
  }

  generatePin() {
    let pin = 0;
    let pinExist = true;

    while (pinExist) {
      pinExist = false;
      this.props.fields.forEach((field) => {
        if (Number(field.values.pin) === Number(pin)) {
          pinExist = true;
        }
      });

      if(pinExist)
        pin++;
    }
    return pin;
  }

  handleCloneField(id) {

    const isNameAlreadyExists = (name) => {
      return this.props.fields.some((field) => {
        if (!field.values || !field.values.name || !name)
          return false;

        return field.values.name.trim() === name.trim();
      });
    };

    const cloned = _.find(this.props.fields, {id: id});

    const nextId = this.props.fields.reduce((acc, value) => (
        acc < value.id ? value.id : acc
      ), this.props.fields.length ? this.props.fields[0].id : 0) + 1;

    let name = '';
    let nameUnique = false;
    let i = 0;

    while (!nameUnique) {
      name = `${cloned.values.name} Copy ${!i ? '' : i}`;
      if (!isNameAlreadyExists(name)) {
        nameUnique = true;
      }
      i++;
    }

    const fields = [
      ...this.props.fields,
      {
        ...cloned,
        id: nextId,
        values: {
          ...cloned.values,
          name: cloned.values && cloned.values.name ? `${name}` : '',
          pin: this.generatePin(),
          isRecentlyCreated: true
        }
      }
    ];

    const originalIndex = _.findIndex(fields, {id: id});

    this.props.onFieldsChange(
      arrayMove(fields, fields.length - 1, originalIndex + 1)
    );
  }

  addDataStreamsField(params) {
    const nextId = this.props.fields.reduce((acc, value) => (
        acc < value.id ? value.id : acc
      ), this.props.fields.length ? this.props.fields[0].id : 0) + 1;

    this.props.onFieldsChange([
      ...this.props.fields,
      {
        id: nextId,
        type: params.type,
        values: {
          ...params.values,
          pin: this.generatePin(),
          units: Unit.None.key,
          isRecentlyCreated: true,
          pinType: 'VIRTUAL',
        }
      }
    ]);

    /** @todo dirty hack, remove it after refactoring */
    setTimeout(() => document.querySelector(`.datastream-name-field-${nextId}  input`).focus(), 100);
  }

  onSortStart() {
    this.setState({
      isSortEnabled: true
    });
  }

  onSortEnd({oldIndex, newIndex}) {

    this.props.onFieldsChange(
      arrayMove(this.props.fields, oldIndex, newIndex)
    );

  }

  render() {

    const className = classnames({
      'no-mouse-selection': this.state.isSortEnabled
    });

    return (
      <div className={className}>
        { this.props.fields && this.props.fields.length && (
          <this.SortableList items={this.props.fields}
                             onSortStart={this.onSortStart.bind(this)}
                             onSortEnd={this.onSortEnd.bind(this)}
                             useDragHandle={true}
                             lockAxis="y"
                             useWindowAsScrollContainer={true}
                             helperClass="product-metadata-item-drag-active"/>) || null
        }
        <AddDataStreamsFields onFieldAdd={this.addDataStreamsField.bind(this)}/>
        <BackTop/>
      </div>
    );
  }
}

export default DataStreams;
