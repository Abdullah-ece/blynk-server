import React from 'react';
import {AddMetadataFields} from 'scenes/Products/components/AddField';
import {BackTop} from 'components';
import {
  Metadata as MetadataService,
  filterDynamicMetadataFields,
  hardcodedRequiredMetadataFieldsNames,
  FORMS,
} from 'services/Products';
import Metadata from "scenes/Products/components/Metadata";
import {MetadataRolesDefault} from 'services/Roles';
import _ from 'lodash';
import {
  SortableContainer,
  SortableElement,
  // arrayMove
} from 'react-sortable-hoc';
import {fromJS} from 'immutable';
import Scroll from 'react-scroll';

// import ImmutablePropTypes from 'react-immutable-proptypes';
import PropTypes from 'prop-types';

import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {change} from 'redux-form';

const MetadataFields = Metadata.Fields;
@connect((state) => state, (dispatch) => ({
  changeForm: bindActionCreators(change, dispatch)
}))
class ProductMetadata extends React.Component {

  static propTypes = {

    fields: PropTypes.object,

    changeForm: PropTypes.func,
    onFieldsChange: PropTypes.func,
  };

  constructor(props) {
    super(props);

    this.onSortEnd = this.onSortEnd.bind(this);
    this.onSortStart = this.onSortStart.bind(this);
    this.addMetadataField = this.addMetadataField.bind(this);
    this.handleCloneField = this.handleCloneField.bind(this);
    this.handleChangeField = this.handleChangeField.bind(this);
    this.handleDeleteField = this.handleDeleteField.bind(this);
    this.metadataFieldValidation = this.metadataFieldValidation.bind(this);

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
      this.props.changeForm(FORMS.PRODUCTS_PRODUCT_CREATE, 'metaFields', fields);
  }

  handleChangeField(/*values, dispatch*/) {

    // if (values.id) {
    //   // updates full entity
    //   this.props.onFieldChange(values);
    // } else {
    //   // updates only values of entity
    //   const field = _.find(this.props.fields, {id: props.id});
    //
    //   this.props.onFieldChange({
    //     ...field,
    //     values
    //   });
    // }

  }

  SortableItem = SortableElement(({value, metaFieldKey, metaFieldIndex}) => {

    const field = value;

    let element;

    const props = {
      id: field.get('id'),
      key: field.get('id'),
      metaFieldKey: metaFieldKey,
      index: metaFieldIndex,
      onDelete: this.handleDeleteField,
      onClone: this.handleCloneField,
      field: field
    };

    if (field.get('type') === MetadataService.Fields.TEXT) {
      element = (
        <MetadataFields.TextField
          {...props}
        />
      );
    }

    if (field.get('type') === MetadataService.Fields.NUMBER) {
      element = (
        <MetadataFields.NumberField
          {...props}
        />
      );
    }

    if (field.get('type') === MetadataService.Fields.COST) {
      element = (
        <MetadataFields.CostField
          {...props}
          initialValues={{
            name: field.values.name,
            price: field.values.price,
            perValue: field.values.perValue,
            units: field.values.units,
            currency: field.values.currency,
            role: field.values.role
          }}
        />
      );
    }

    if (field.type === MetadataService.Fields.TIME) {
      element = (
        <MetadataFields.TimeField
          {...props}
          initialValues={{
            name: field.values.name,
            time: field.values.time,
            role: field.values.role
          }}
        />
      );
    }

    if (field.get('type') === MetadataService.Fields.RANGE) {
      element = (
        <MetadataFields.ShiftField
          {...props}
          initialValues={{
            name: field.values.name,
            from: field.values.from,
            role: field.values.role,
            to: field.values.to
          }}
        />
      );
    }

    if (field.type === MetadataService.Fields.SWITCH) {
      element = (
        <MetadataFields.SwitchField
          {...props}
          initialValues={{
            name: field.values.name,
            from: field.values.from,
            value: field.values.value || 0,
            role: field.values.role,
            to: field.values.to
          }}
        />
      );
    }

    if (field.get('type') === MetadataService.Fields.DATE) {
      element = (
        <MetadataFields.DateField
          {...props}
          initialValues={{
            name: field.values.name,
            value: field.values.value
          }}
        />
      );
    }

    if (field.type === MetadataService.Fields.COORDINATES) {
      element = (
        <MetadataFields.CoordinatesField
          {...props}
          initialValues={{
            name: field.values.name,
            lat: field.values.lat,
            role: field.values.role,
            lon: field.values.lon
          }}
        />
      );
    }

    if (field.get('type') === MetadataService.Fields.UNIT) {
      element = (
        <MetadataFields.UnitField
          {...props}
          initialValues={{
            name: field.values.name,
            value: field.values.value,
            role: field.values.role,
            units: field.values.units
          }}
        />
      );
    }

    if (field.type === MetadataService.Fields.CONTACT) {
      element = (
        <MetadataFields.ContactField
          {...props}
          initialValues={{
            name: field.values.name,
            role: field.values.role,
            isDefaultsEnabled: field.values.isDefaultsEnabled,
            firstName: field.values.firstName,
            lastName: field.values.lastName,
            email: field.values.email,
            phone: field.values.phone,
            streetAddress: field.values.streetAddress,
            city: field.values.city,
            state: field.values.state,
            zip: field.values.zip,
            isFirstNameEnabled: field.values.isFirstNameEnabled,
            isLastNameEnabled: field.values.isLastNameEnabled,
            isEmailEnabled: field.values.isEmailEnabled,
            isPhoneEnabled: field.values.isPhoneEnabled,
            isStreetAddressEnabled: field.values.isStreetAddressEnabled,
            isCityEnabled: field.values.isCityEnabled,
            isStateEnabled: field.values.isStateEnabled,
            isZipEnabled: field.values.isZipEnabled
          }}
        />
      );
    }

    if (field.get('type') === MetadataService.Fields.ADDRESS) {
      element = (
        <MetadataFields.AddressField
          {...props}
          initialValues={{
            name: field.values.name,
            role: field.values.role,
            streetAddress: field.values.streetAddress,
            city: field.values.city,
            state: field.values.state,
            zip: field.values.zip,
            country: field.values.country
          }}
        />
      );
    }

    return element || null;

  });

  SortableList = SortableContainer(({items}) => {
    return (
      <div>
        {items.map((item, index) => {
          return (
            <this.SortableItem key={`item-${item.get('id')}`} metaFieldKey={item.get('metaFieldKey')} metaFieldIndex={index} index={index} value={item}/>
          );
        })}
      </div>
    );
  });

  metadataFieldValidation(values, props) {
    const errors = {};

    this.props.fields.forEach((field) => {
      if (field.values.name === values.name && Number(props.id) !== Number(field.id)) {
        errors.name = 'Name should be unique';
      }
    });

    return errors;
  }

  handleCloneField(id) {

    const isNameAlreadyExists = (name) => {
      return this.props.fields.getAll().some((field) => {
        return field.name.trim() === name.trim();
      });
    };

    const cloned = _.find(this.props.fields.getAll(), {id: id});

    let name = '';
    let nameUnique = false;
    let i = 0;

    while (!nameUnique) {
      name = `${cloned.name} Copy ${!i ? '' : i}`.trim();
      if (!isNameAlreadyExists(name)) {
        nameUnique = true;
      }
      i++;
    }

    const originalIndex = _.findIndex(this.props.fields.getAll(), {id: id});

    this.props.fields.push({
      ...cloned,
      name: name,
      id: new Date().getTime()
    });

    const newIndex = this.props.fields.getAll().length;
    const oldIndex = originalIndex + 1;

    if(newIndex !== oldIndex)
      this.props.fields.swap(newIndex, oldIndex);

  }

  // addMetadataField(params) {
  //
  //   const nextId = _.random(4, 2000000000);
  //
  //   this.props.onFieldsChange([
  //     ...this.props.fields,
  //     {
  //       id: nextId,
  //       type: params.type,
  //       values: {
  //         role: MetadataRolesDefault,
  //         name: '',
  //         value: '',
  //         ...params.values,
  //         isRecentlyCreated: true
  //       }
  //     }
  //   ]);
  //
  //   /** @todo dirty hack, remove it after refactoring */
  //   setTimeout(() => document.querySelector(`.metadata-name-field-${nextId}  input`).focus(), 100);
  // }

  handleDeleteField(key) {
    let fieldIndex = null;

    this.props.fields.getAll().forEach((field, index) => {
      if(Number(field.id) === Number(key))
        fieldIndex = index;
    });

    this.props.fields.remove(fieldIndex);
  }

  getStaticFields({ metaFields }) {

    const elements = [];

    metaFields.forEach((field, key) => {

      if (!field.has('name')) return false;

      const props = {
        id: field.get('id'),
        key: key,
        metaFieldKey: key,
        field: field,
        tools: false,
      };

      if (field.get('name') === hardcodedRequiredMetadataFieldsNames.LocationName) {
        elements.push(
          <MetadataFields.LocationField {...props}/>
        );
      }

      if (field.get('name') === hardcodedRequiredMetadataFieldsNames.DeviceOwner) {
        elements.push(
          <MetadataFields.DeviceOwnerField {...props}/>
        );
      }

      if (field.get('name') === hardcodedRequiredMetadataFieldsNames.DeviceName) {
        elements.push(
          <MetadataFields.DeviceNameField {...props}/>
        );
      }

      if (field.get('name') === hardcodedRequiredMetadataFieldsNames.Manufacturer) {
        elements.push(
          <MetadataFields.ManufacturerField {...props}/>
        );
      }

      if (field.get('name') === hardcodedRequiredMetadataFieldsNames.ModelName) {
        elements.push(
          <MetadataFields.ModelNameField {...props}/>
        );
      }

      if (field.get('name') === hardcodedRequiredMetadataFieldsNames.TimezoneOfTheDevice) {
        elements.push(
          <MetadataFields.TimezoneOfDeviceField {...props}/>
        );
      }

    });

    return elements;

  }

  onSortStart() {
    this.setState({
      isSortEnabled: true
    });
  }

  addMetadataField(params) {

    const nextId = new Date().getTime();
    this.props.fields.push({
      id: nextId,
      role: MetadataRolesDefault,
      isRecentlyCreated: true,
      ...params,
    });

    /* @todo refactor remove timeout */
    setTimeout(() => document.querySelector(`.metadata-name-field-${nextId}  input`).focus(), 100);
  }

  getDynamicMetaFields() {
    const metaFields = fromJS(this.props.fields.getAll()).map((item, index) => item.set('metaFieldKey', index));

    return filterDynamicMetadataFields(metaFields);
  }

  onSortEnd(params) {

    if(params.newIndex === params.oldIndex)
      return false;

    const dynamicMetaFields = this.getDynamicMetaFields();

    this.props.fields.swap(dynamicMetaFields.get(params.oldIndex).get('metaFieldKey'), dynamicMetaFields.get(params.newIndex).get('metaFieldKey'));
  }

  render() {

    /*
    * fields - reduxForm FieldArray with additional params to operate form
    * metaFields - simple immutable List with fields data
    * */

    /*
      metaFieldKey is using in forms to access deep fields like: formValues.metaFields.$index$.name etc.
      metaField is an index of metaField on reduxForm storage
     */

    const metaFields = fromJS(this.props.fields.getAll()).map((item, index) => item.set('metaFieldKey', index));

    const dynamicMetaFields = this.getDynamicMetaFields();

    return (
      <div className={this.state.isSortEnabled ? 'no-mouse-selection' : null}>
        <Metadata.ItemsList>

          {this.getStaticFields({
            reduxFormFields: this.props.fields,
            metaFields: metaFields,
          })}

          { metaFields && metaFields.size && (
            <this.SortableList items={dynamicMetaFields}
                               useWindowAsScrollContainer={true}
                               onSortEnd={this.onSortEnd}
                               onSortStart={this.onSortStart}
                               useDragHandle={true}
                               lockAxis="y"
                               helperClass="product-metadata-item-drag-active"/>) || null
          }

        </Metadata.ItemsList>

        <AddMetadataFields onFieldAdd={this.addMetadataField}/>
        <BackTop/>
      </div>
    );
  }
}

export default ProductMetadata;
