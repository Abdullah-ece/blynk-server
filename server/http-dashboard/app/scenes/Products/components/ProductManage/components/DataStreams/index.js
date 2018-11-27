import React from 'react';
import Scroll from 'react-scroll';
// import {BackTop} from 'components';
import {AddDataStreamsFields} from 'scenes/Products/components/AddField';
import {DataStreamsBaseField, DataStreamsItemsList} from "scenes/Products/components/DataStreams";
import {fromJS} from 'immutable';
import {Unit, FORMS, isDataStreamPristine, getNextId} from "services/Products";
import {SortableContainer, SortableElement} from 'react-sortable-hoc';
import _ from 'lodash';
import {change, getFormSyncErrors, getFormValues} from 'redux-form';
import classnames from 'classnames';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';

@connect((state) => ({
  formValues: getFormValues(FORMS.PRODUCTS_PRODUCT_MANAGE)(state),
  formSyncErrors: fromJS(getFormSyncErrors(FORMS.PRODUCTS_PRODUCT_MANAGE)(state) || {})
}), (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
class List extends React.Component {

  static propTypes = {
    formValues: PropTypes.object,
    fields: PropTypes.object,

    formSyncErrors: PropTypes.object,

    changeForm: PropTypes.func
  };

  constructor(props) {
    super(props);

    this.onSortEnd = this.onSortEnd.bind(this);
    this.onSortStart = this.onSortStart.bind(this);
    // this.fieldsValidation = this.fieldsValidation.bind(this);
    this.handleCloneField = this.handleCloneField.bind(this);
    // this.handleChangeField = this.handleChangeField.bind(this);
    this.handleDeleteField = this.handleDeleteField.bind(this);
    this.addDataStreamsField = this.addDataStreamsField.bind(this);
    this.handleChange = this.handleChange.bind(this);
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
      this.props.changeForm(FORMS.PRODUCTS_PRODUCT_MANAGE, 'dataStreams', fields);
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

    const fieldIndex = name.match(/dataStreams\[([0-9]+)\]/)[1]; // dataStreams[0]

    const fieldSyncErrors = this.props.formSyncErrors.getIn(['dataStreams', fieldIndex]) || fromJS({});

    const props = {
      key: field.get('id'),
      onDelete: this.handleDeleteField,
      onClone: this.handleCloneField,
      field: field,
      fieldSyncErrors: fieldSyncErrors,
      isDirty: !isDataStreamPristine(field),
      onChange: this.handleChange,
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

  handleChange(dataStream) {

    // update all widgets which have updated dataStream to keep all consistent

    let widgets = this.props.formValues.webDashboard.widgets;
    
    widgets = widgets.map((widget) => ({
      ...widget,
      sources: widget.sources ? widget.sources.map((source) => {
        if(source && source.dataStream && Number(source.dataStream.id) === Number(dataStream.id)) {
          return {
            ...source,
            dataStream: {
              ...dataStream
            }
          };
        }
        return source;
      }) : []
    }));

    this.props.changeForm(FORMS.PRODUCTS_PRODUCT_MANAGE, 'webDashboard.widgets', widgets);

    // [0].sources[0].dataStream
  }

  handleDeleteField(id) {

    let fieldIndex = null;

    this.props.fields.getAll().forEach((field, index) => {
      if(Number(field.id) === Number(id))
        fieldIndex = index;
    });

    if(fieldIndex !== null)
      this.props.fields.remove(fieldIndex);
  }

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

  handleCloneField(id) {

    const isNameAlreadyExists = (label) => {
      return this.props.fields.getAll().some((field) => {
        return field.label && field.label.trim() === label.trim();
      });
    };

    const cloned = _.find(this.props.fields.getAll(), {id: id});

    const nextId = getNextId(this.props.fields.getAll());

    let name = '';
    let nameUnique = !cloned.label; //if cloned name is empty don't find unique name
    let i = 0;

    while (!nameUnique) {
      name = `${cloned.label} Copy ${!i ? '' : i}`.trim();
      if (!isNameAlreadyExists(name)) {
        nameUnique = true;
      }
      i++;
    }

    const field = {
      ...cloned,
      id: nextId,
      label: name,
      pin: this.generatePin(),
      isRecentlyCreated: true
    };

    const originalIndex = _.findIndex(this.props.fields.getAll(), {id: id});

    this.props.fields.push(field);

    const newIndex = this.props.fields.getAll().length;
    const oldIndex = originalIndex + 1;

    if(newIndex !== oldIndex)
      this.props.fields.swap(newIndex, oldIndex);
  }

  addDataStreamsField() {

    const nextId = getNextId(this.props.fields.getAll());

    this.props.fields.push({
      id: nextId,
      pin: `${this.generatePin()}`,
      units: Unit.None.key,
      isRecentlyCreated: true,
      pinType: 'VIRTUAL',
    });

    // /** @todo dirty hack, remove it after refactoring */
    setTimeout(() => document.querySelector(`.datastream-name-field-${nextId}  input`).focus(), 100);
  }

  onSortStart() {
    this.setState({
      isSortEnabled: true
    });
  }

  onSortEnd({oldIndex, newIndex}) {
    if(newIndex === oldIndex)
      return false;

    this.props.fields.swap(oldIndex, newIndex);
  }

  render() {

    const className = classnames({
      'no-mouse-selection': this.state.isSortEnabled
    });

    return (
      <div className={className}>

        {this.props.fields && this.props.fields.length && (
          <this.SortableList items={this.props.fields}
                             onSortStart={this.onSortStart}
                             onSortEnd={this.onSortEnd}
                             useDragHandle={true}
                             lockAxis="y"
                             useWindowAsScrollContainer={true}
                             helperClass="product-metadata-item-drag-active"
          />
        ) || null}

        <AddDataStreamsFields onFieldAdd={this.addDataStreamsField}/>

        {/*<BackTop/>*/}
      </div>
    );


  }
}

export default List;
