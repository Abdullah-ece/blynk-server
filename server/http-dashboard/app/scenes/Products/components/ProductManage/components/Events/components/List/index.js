import React from 'react';
// import Scroll from 'react-scroll';
// import {BackTop} from 'components';
import {Online, Offline, Info, Warning, Critical, Add} from 'scenes/Products/components/Events';
import {EVENT_TYPES} from 'services/Products';
// import {getNextId} from 'services/Entity';
import {SortableContainer, SortableElement} from 'react-sortable-hoc';
// import _ from 'lodash';
import classnames from 'classnames';
// import {connect} from 'react-redux';
import {fromJS} from 'immutable';
//
// @connect((state, ownProps) => ({
//   fields: fromJS(ownProps.fields)
// }))
import PropTypes from 'prop-types';

class List extends React.Component {

  static propTypes = {
    fields: PropTypes.object,

    // onFieldsChange: React.PropTypes.func
  };

  constructor(props) {
    super(props);

    // this.onSortEnd = this.onSortEnd.bind(this);
    // this.onSortStart = this.onSortStart.bind(this);
    this.handleAddField = this.handleAddField.bind(this);
    // this.handleFieldClone = this.handleFieldClone.bind(this);
    // this.handleFieldChange = this.handleFieldChange.bind(this);
    // this.handleFieldDelete = this.handleFieldDelete.bind(this);
    // this.handleFieldValidation = this.handleFieldValidation.bind(this);

  }


  state = {
    isSortEnabled: false
  };

  // componentDidUpdate() {
  //   this.props.fields.forEach((field) => {
  //     if (field && field.values && field.values.isRecentlyCreated) {
  //
  //       Scroll.scroller.scrollTo(`${field.name}`, {
  //         duration: 1000,
  //         offset: -64,
  //         smooth: "easeInOutQuint",
  //       });
  //
  //       this.handleFieldChange({
  //         ...field.values,
  //         id: field.id,
  //         isRecentlyCreated: false
  //       });
  //     }
  //   });
  // }
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
  // handleFieldDelete(values) {
  //   if (values.id) {
  //
  //     let fields = this.props.fields.filter(
  //       (field) => Number(field.id) !== Number(values.id)
  //     );
  //
  //     this.props.onFieldsChange(fields);
  //
  //   } else {
  //     throw Error('Missing id parameter for handleFieldDelete');
  //   }
  // }
  //
  // handleFieldClone(values) {
  //
  //   const isNameAlreadyExists = (name) => {
  //     return this.props.fields.some((field) => {
  //       if (!field.values || !field.values.name || !name)
  //         return false;
  //
  //       return field.values.name.trim() === name.trim();
  //     });
  //   };
  //
  //   if (values.id) {
  //
  //     const originalIndex = _.findIndex(this.props.fields, {id: values.id});
  //     const original = this.props.fields[originalIndex];
  //
  //     let name = '';
  //     let eventCode = '';
  //     let nameUnique = false;
  //     let i = 0;
  //
  //     while (!nameUnique) {
  //       name = `${original.values.name} Copy ${!i ? '' : i}`;
  //       eventCode = `${original.values.eventCode}_copy${!i ? '' : '_' + i}`;
  //       if (!isNameAlreadyExists(name)) {
  //         nameUnique = true;
  //       }
  //       i++;
  //     }
  //
  //     const copy = {
  //       ...original,
  //       values: {
  //         ...original.values,
  //         name: original.values.name ? name : '',
  //         eventCode: original.values.eventCode ? eventCode : '',
  //         isRecentlyCreated: true
  //       },
  //       id: getNextId(this.props.fields)
  //     };
  //
  //     this.props.onFieldsChange(arrayMove([
  //       ...this.props.fields,
  //       copy
  //     ], this.props.fields.length, originalIndex + 1));
  //
  //   } else {
  //     throw Error('Missing id parameter for handleFieldClone');
  //   }
  // }
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
  //         onDelete: this.handleFieldDelete,
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

    const nextId = new Date().getTime();

    this.props.fields.push({
      id: nextId,
      type: type,
      isRecentlyCreated: true
    });

    /** @todo dirty hack, remove it after refactoring */
    // setTimeout(() => document.querySelector(`.event-name-field-${nextId}  input`).focus(), 100);
  }
  //
  //
  // SortableList = SortableContainer(({items}) => {
  //   return (
  //     <div>
  //       {items.map((value, index) => {
  //         return (
  //           <this.SortableItem key={`item-${value.id}`} index={index} value={value}/>
  //         );
  //       })}
  //     </div>
  //   );
  // });
  //
  // onSortStart() {
  //   this.setState({
  //     isSortEnabled: true
  //   });
  // }
  //
  // onSortEnd({oldIndex, newIndex}) {
  //   this.setState({
  //     isSortEnabled: false
  //   });
  //
  //   const COUNT_OF_STATIC_FIELDS = 2;
  //   this.props.onFieldsChange(
  //     arrayMove(this.props.fields, oldIndex + COUNT_OF_STATIC_FIELDS, newIndex + COUNT_OF_STATIC_FIELDS)
  //   );
  // }

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
        key: field.get('id')
        // onClone: this.handleFieldClone,
        // onDelete: this.handleFieldDelete,
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

  SortableItem = SortableElement(({value}) => {

    const field = value;

    let options = {
      key: `event${field.get('id')}`,
      field: field,
      // onDelete: this.handleFieldDelete,
      // onClone: this.handleFieldClone,
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

  SortableList = SortableContainer(({items}) => {

    return (
      <div>
        {items.map((item, index) => {
          return (
            <this.SortableItem key={`item-${item.get('id')}`} index={index} value={item}/>
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
        // onSortStart={this.onSortStart}
        // onSortEnd={this.onSortEnd}
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
