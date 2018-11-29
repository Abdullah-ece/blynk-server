import React from 'react';
import Scroll from 'react-scroll';
// import {BackTop} from 'components';
import {
  Online,
  Offline,
  Info,
  Warning,
  Critical,
  Add
} from 'scenes/Products/components/Events';
import { EVENT_TYPES, FORMS, getNextId } from 'services/Products';
// import {getNextId} from 'services/Entity';
import { SortableContainer, SortableElement } from 'react-sortable-hoc';
import _ from 'lodash';
import classnames from 'classnames';
import { fromJS } from 'immutable';

import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { change } from 'redux-form';

@connect((state) => state, (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
class List extends React.Component {

  static propTypes = {
    fields: PropTypes.object,

    changeForm: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    this.onSortEnd = this.onSortEnd.bind(this);
    this.onSortStart = this.onSortStart.bind(this);
    this.handleAddField = this.handleAddField.bind(this);
    this.handleCloneField = this.handleCloneField.bind(this);
    this.handleDeleteField = this.handleDeleteField.bind(this);
    // this.handleFieldValidation = this.handleFieldValidation.bind(this);

  }


  state = {
    isSortEnabled: false
  };

  componentDidUpdate() {

    // all new fields marked as isRecentlyCreated to be able to scroll to new element
    // after scrolled to element we should remove isRecentlyCreated to prevent scroll every update

    let fields = [];
    let shouldUpdateFields = false;

    this.props.fields.getAll().forEach((field) => {

      if (!field.isRecentlyCreated) {
        return fields.push(field);
      }

      shouldUpdateFields = true;

      Scroll.scroller.scrollTo(`${field.name}`, {
        duration: 1000,
        offset: -64,
        smooth: "easeInOutQuint",
      });

      return fields.push({
        ...field,
        isRecentlyCreated: false,
      });
    });

    if (shouldUpdateFields)
      this.props.changeForm(FORMS.PRODUCTS_PRODUCT_MANAGE, 'events', fields);
  }

  //
  // handleFieldChange(values, /*dispatch, props*/) {
  //   if (values.id) {
  //
  //     let fields = [...this.props.fields];
  //
  //     const fieldIndex = _.findIndex(this.props.fields, {id: values.id});
  //
  //     fields[fieldIndex] = {
  //       ...fields[fieldIndex],
  //       values: {
  //         ...values
  //       }
  //     };
  //
  //     this.props.onFieldsChange(
  //       fields
  //     );
  //
  //   } else {
  //     throw Error('Missing id parameter for handleFieldChange');
  //   }
  // }
  //
  // handleFieldValidation(values, props) {
  //   const errors = {};
  //
  //   this.props.fields.forEach((field) => {
  //     if (values.name && field.values.name === values.name && Number(props.fields.id) && Number(props.fields.id) !== Number(field.id)) {
  //       errors.name = 'Name should be unique';
  //     }
  //     if (values.eventCode && field.values.eventCode === values.eventCode && Number(props.fields.id) !== Number(field.id)) {
  //       errors.eventCode = 'Code should be unique';
  //     }
  //   });
  //
  //   return errors;
  // }
  //
  handleDeleteField(id) {
    let fieldIndex = null;

    this.props.fields.getAll().forEach((field, index) => {
      if (Number(field.id) === Number(id))
        fieldIndex = index;
    });

    this.props.fields.remove(fieldIndex);
  }

  handleCloneField(id) {

    const isValueAlreadyExists = (property, value) => {
      return this.props.fields.getAll().some((field) => {
        return field[property] && field[property].trim() === value.trim();
      });
    };

    const getUniqueValue = (property, oldValue) => {
      let value = '';
      let uniqueValue = !oldValue;

      while (!uniqueValue) {
        value = `${oldValue || ''}${property === "eventCode" ? "_copy" : " Copy"}`;
        if (!isValueAlreadyExists(property, value)) {
          uniqueValue = true;
        }
      }

      return value;
    };

    const cloned = _.find(this.props.fields.getAll(), { id: id });

    const name = getUniqueValue("name", cloned.name);
    const eventCode = getUniqueValue("eventCode", cloned.eventCode);

    const originalIndex = _.findIndex(this.props.fields.getAll(), { id: id });

    this.props.fields.push({
      ...cloned,
      name: name,
      eventCode: eventCode,
      id: getNextId(this.props.fields.getAll()),
      isRecentlyCreated: true,
    });

    const newIndex = this.props.fields.getAll().length;
    const oldIndex = originalIndex + 1;

    if (newIndex !== oldIndex)
      this.props.fields.move(newIndex, oldIndex);

  }

  //
  // getFieldsForTypes(fields, types) {
  //   const elements = [];
  //
  //   const filterByTypes = (field) => types.indexOf(field.type) !== -1;
  //
  //   if (fields && Array.isArray(fields)) {
  //
  //     fields.filter(filterByTypes).forEach((field) => {
  //
  //       let options = {
  //         form: `event${field.id}`,
  //         key: `event${field.id}`,
  //         initialValues: {
  //           id: field.id,
  //           name: field.values.name,
  //           isNotificationsEnabled: field.values.isNotificationsEnabled,
  //           emailNotifications: field.values.emailNotifications && field.values.emailNotifications.map((value) => value.toString()),
  //           pushNotifications: field.values.pushNotifications && field.values.pushNotifications.map((value) => value.toString()),
  //         },
  //         onChange: this.handleFieldChange,
  //         onDelete: this.handleDeleteField,
  //         onClone: this.handleFieldClone,
  //         validate: this.handleFieldValidation,
  //       };
  //
  //       if (field.type === EVENT_TYPES.ONLINE) {
  //         elements.push(
  //           <Online {...options}/>
  //         );
  //       }
  //
  //       if (field.type === EVENT_TYPES.OFFLINE) {
  //
  //         options = {
  //           ...options,
  //           initialValues: {
  //             ...options.initialValues,
  //             ignorePeriod: field.values.ignorePeriod
  //           }
  //         };
  //
  //         elements.push(
  //           <Offline {...options}/>
  //         );
  //       }
  //
  //     });
  //   }
  //
  //   return elements;
  // }
  //
  // getStaticFields(fields) {
  //   return this.getFieldsForTypes(fields, [EVENT_TYPES.ONLINE, EVENT_TYPES.OFFLINE]);
  // }
  //
  // getDynamicFields(fields) {
  //   return fields.filter((field) => (
  //     [EVENT_TYPES.INFO, EVENT_TYPES.WARNING, EVENT_TYPES.CRITICAL].indexOf(field.type) !== -1
  //   ));
  // }
  //
  handleAddField(type) {

    const nextId = getNextId(this.props.fields.getAll());

    this.props.fields.push({
      id: nextId,
      type: type,
      isRecentlyCreated: true
    });

    /** @todo dirty hack, remove it after refactoring */
    setTimeout(() => document.querySelector(`.event-name-field-${nextId}  input`).focus(), 100);
  }

  //
  //
  SortableList = SortableContainer(({ items }) => {
    return (
      <div>
        {items.map((value, index) => {
          return (
            <this.SortableItem key={`item-${value.id}`} index={index}
                               value={value}/>
          );
        })}
      </div>
    );
  });

  onSortStart() {
    this.setState({
      isSortEnabled: true
    });
  }

  onSortEnd({ oldIndex, newIndex }) {
    if (newIndex === oldIndex)
      return false;

    let dynamicFields = this.getDynamicFields();
    this.props.fields.move(dynamicFields[oldIndex].get('eventKey'), dynamicFields[newIndex].get('eventKey'));
  }

  getFieldsByTypes(types) {
    return this.props.fields.map((name, index, fields) => {
      const field = fromJS(fields.get(index));
      return field.set('eventKey', index)
        .set('fieldPrefix', name);

    }).filter((field) => types.indexOf(field.get('type')) !== -1);
  }

  getStaticFields() {
    return this.getFieldsByTypes([EVENT_TYPES.ONLINE, EVENT_TYPES.OFFLINE]);
  }

  getDynamicFields() {
    return this.getFieldsByTypes([EVENT_TYPES.CRITICAL, EVENT_TYPES.INFO, EVENT_TYPES.WARNING]);
  }

  renderStaticFields() {
    const fields = this.getStaticFields();

    return fields.map((field) => {
      let props = {
        field: field,
        key: field.get('id'),
        onClone: this.handleCloneField,
        onDelete: this.handleDeleteField,
        // isDirty: false
      };

      if (field.get('type') === EVENT_TYPES.ONLINE)
        return (
          <Online {...props}/>
        );

      if (field.get('type') === EVENT_TYPES.OFFLINE)
        return (
          <Offline {...props}/>
        );
    });
  }

  SortableItem = SortableElement(({ value }) => {

    const field = value;

    let options = {
      key: `event${field.get('id')}`,
      field: field,
      onDelete: this.handleDeleteField,
      onClone: this.handleCloneField,
    };

    if (field.get('type') === EVENT_TYPES.INFO)
      return (
        <Info {...options}/>
      );


    if (field.get('type') === EVENT_TYPES.WARNING)
      return (
        <Warning {...options}/>
      );


    if (field.get('type') === EVENT_TYPES.CRITICAL)
      return (
        <Critical {...options}/>
      );

    return null;

  });

  SortableList = SortableContainer(({ items }) => {

    return (
      <div>
        {items.map((item, index) => {
          return (
            <this.SortableItem key={`item-${item.get('id')}`} index={index}
                               value={item}/>
          );
        })}
      </div>
    );
  });

  renderDynamicFields() {
    const fields = this.getDynamicFields();

    return (

      <this.SortableList items={fields}
                         useDragHandle={true}
                         useWindowAsScrollContainer={true}
                         onSortStart={this.onSortStart}
                         onSortEnd={this.onSortEnd}
                         lockAxis="y"
                         helperClass="product-events-item-drag-active"/>

    );
  }

  render() {

    // const staticFields = this.getStaticFields(this.props.fields);
    //
    // const className = classnames({
    //   'product-events-list': true,
    //   'no-mouse-selection': this.state.isSortEnabled
    // });

    /*
    <div className={className}>
    { staticFields }

    <this.SortableList items={this.getDynamicFields(this.props.fields)} onSortEnd={this.onSortEnd}
    useDragHandle={true}
    useWindowAsScrollContainer={true}
    onSortStart={this.onSortStart}
    onSortEnd={this.onSortEnd}
    lockAxis="y"
    helperClass="product-events-item-drag-active"/>

      <Add handleSubmit={this.handleAddField}/>
    <BackTop/>
    </div>

    */

    const className = classnames({
      'product-events-list': true,
      'no-mouse-selection': this.state.isSortEnabled
    });

    return (

      <div className={className}>

        {this.renderStaticFields()}

        {this.renderDynamicFields()}

        <Add handleSubmit={this.handleAddField}/>

      </div>

    );
  }

}

export default List;
