import React from 'react';
import Scroll from 'react-scroll';
import {BackTop} from 'components';
import {AddDataStreamsFields} from 'scenes/Products/components/AddField';
import {DataStreamsBaseField, DataStreamsItemsList} from "scenes/Products/components/DataStreams";
import {fromJS} from 'immutable';
import {Unit, FORMS} from "services/Products";
import {SortableContainer, SortableElement} from 'react-sortable-hoc';
// import _ from 'lodash';
import {change} from 'redux-form';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

@connect((state) => state, (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
class List extends React.Component {

  static propTypes = {
    fields: PropTypes.object,

    changeForm: PropTypes.func
  };

  constructor(props) {
    super(props);

    // this.onSortEnd = this.onSortEnd.bind(this);
    // this.onSortStart = this.onSortStart.bind(this);
    // this.fieldsValidation = this.fieldsValidation.bind(this);
    // this.handleCloneField = this.handleCloneField.bind(this);
    // this.handleChangeField = this.handleChangeField.bind(this);
    // this.handleDeleteField = this.handleDeleteField.bind(this);
    this.addDataStreamsField = this.addDataStreamsField.bind(this);

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

    if(shouldUpdateFields)
      this.props.changeForm(FORMS.PRODUCTS_PRODUCT_CREATE, 'dataStreams', fields);
  }

  // componentDidUpdate() {
  //   this.props.fields.getAll().forEach((field) => {
  //     if (field && field && field.values.isRecentlyCreated) {
  //
  //       Scroll.scroller.scrollTo(`${field.name}`, {
  //         duration: 1000,
  //         offset: -64,
  //         smooth: "easeInOutQuint",
  //       });
  //
  //       this.handleChangeField({
  //         ...field,
  //         values: {
  //           ...field.values,
  //           isRecentlyCreated: false
  //         }
  //       });
  //     }
  //   });
  // }

  SortableItem = SortableElement(({value, name}) => {

    const field = fromJS(value);

    const props = {
      key: field.get('id'),
      // onChange: this.handleChangeField,
      // validate: this.fieldsValidation,
      // onDelete: this.handleDeleteField,
      // onClone: this.handleCloneField,
      field: field,
      name: name,
    };

    let element = (<DataStreamsBaseField {...props} />);

    return element;

  });

  SortableList = SortableContainer(({items}) => {
    return (
      <DataStreamsItemsList>
        {items.map((name, index, fields) => {
          const field = fields.get(index);
          return (
            <this.SortableItem key={`item-${field.id}`} index={index} value={field} name={name}/>
          );
        })}
      </DataStreamsItemsList>
    );
  });

  // handleChangeField(values, dispatch, props) {
  //   if (values.id) {
  //     // updates full entity
  //     this.props.onFieldChange(values);
  //   } else {
  //     // updates only values of entity
  //     const field = _.find(this.props.fields, {id: props.id});
  //
  //     this.props.onFieldChange({
  //       ...field,
  //       values: {
  //         ...field.values,
  //         ...values,
  //       }
  //     });
  //   }
  // }

  // fieldsValidation(values, props) {
  //   const errors = {};
  //
  //   this.props.fields.forEach((field) => {
  //     if (field.values.name === values.name && Number(props.id) !== Number(field.id)) {
  //       errors.name = 'Name should be unique';
  //     }
  //     if (Number(field.values.pin) === Number(values.pin) && Number(props.id) !== Number(field.id)) {
  //       errors.pin = 'Pin should be unique';
  //     }
  //   });
  //
  //   return errors;
  // }

  // handleDeleteField(key) {
  //   this.props.onFieldsChange(this.props.fields.filter((field) => field.id !== key));
  // }

  generatePin() {
    let pin = 0;
    let pinExist = true;

    if (!this.props.fields.length)
      return pin;

    while (pinExist) {
      pinExist = false;
      this.props.fields.getAll().forEach((field) => {
        if (Number(field.pin) === Number(pin)) {
          pinExist = true;
        }
      });

      if (pinExist)
        pin++;
    }
    return pin;
  }

  // handleCloneField(id) {
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
  //   const cloned = _.find(this.props.fields, {id: id});
  //
  //   const nextId = this.props.fields.reduce((acc, value) => (
  //     acc < value.id ? value.id : acc
  //   ), this.props.fields.length ? this.props.fields[0].id : 0) + 1;
  //
  //   let name = '';
  //   let nameUnique = false;
  //   let i = 0;
  //
  //   while (!nameUnique) {
  //     name = `${cloned.values.name} Copy ${!i ? '' : i}`;
  //     if (!isNameAlreadyExists(name)) {
  //       nameUnique = true;
  //     }
  //     i++;
  //   }
  //
  //   const fields = [
  //     ...this.props.fields,
  //     {
  //       ...cloned,
  //       id: nextId,
  //       values: {
  //         ...cloned.values,
  //         name: cloned.values && cloned.values.name ? `${name}` : '',
  //         pin: this.generatePin(),
  //         isRecentlyCreated: true
  //       }
  //     }
  //   ];
  //
  //   const originalIndex = _.findIndex(fields, {id: id});
  //
  //   this.props.onFieldsChange(
  //     arrayMove(fields, fields.length - 1, originalIndex + 1)
  //   );
  // }

  addDataStreamsField(params) {

    const nextId = new Date().getTime();

    this.props.fields.push({
      id: nextId,
      type: params.type,
      pin: `${this.generatePin()}`,
      units: Unit.None.key,
      isRecentlyCreated: true,
      pinType: 'VIRTUAL',
    });

    // /** @todo dirty hack, remove it after refactoring */
    // setTimeout(() => document.querySelector(`.datastream-name-field-${nextId}  input`).focus(), 100);
  }

  // onSortStart() {
  //   this.setState({
  //     isSortEnabled: true
  //   });
  // }
  //
  // onSortEnd({oldIndex, newIndex}) {
  //
  //   this.props.onFieldsChange(
  //     arrayMove(this.props.fields, oldIndex, newIndex)
  //   );
  //
  // }

  render() {

    const className = classnames({
      'no-mouse-selection': this.state.isSortEnabled
    });


    return (
      <div className={className}>

        {this.props.fields && this.props.fields.length && (
          <this.SortableList items={this.props.fields}
            // onSortStart={this.onSortStart}
            // onSortEnd={this.onSortEnd}
                             useDragHandle={true}
                             lockAxis="y"
                             useWindowAsScrollContainer={true}
                             helperClass="product-metadata-item-drag-active"/>
        ) || null}

        <AddDataStreamsFields onFieldAdd={this.addDataStreamsField}/>

        <BackTop/>
      </div>
    );


  }
}

export default List;
