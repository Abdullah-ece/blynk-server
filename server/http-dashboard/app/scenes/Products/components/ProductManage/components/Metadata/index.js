import React from 'react';
import {AddMetadataFields} from 'scenes/Products/components/AddField';
import {
  Metadata as MetadataService,
  filterDynamicMetadataFields,
  filterHardcodedMetadataFields,
  hardcodedRequiredMetadataFieldsNames
} from 'services/Products';
import Metadata from "scenes/Products/components/Metadata";
import {MetadataRolesDefault} from 'services/Roles';
import _ from 'lodash';
import {SortableContainer, SortableElement, arrayMove} from 'react-sortable-hoc';
const MetadataFields = Metadata.Fields;
class ProductMetadata extends React.Component {

  static propTypes = {

    fields: React.PropTypes.array,

    onEventsChange: React.PropTypes.func,
    onFieldChange: React.PropTypes.func,
    onFieldsChange: React.PropTypes.func
  };

  constructor(props) {
    super(props);
  }

  handleChangeField(values, dispatch, props) {

    if (values.id) {
      // updates full entity
      this.props.onFieldChange(values);
    } else {
      // updates only values of entity
      const field = _.find(this.props.fields, {id: props.id});

      this.props.onFieldChange({
        ...field,
        values
      });
    }
  }

  SortableItem = SortableElement(({value}) => {

    const field = value;

    const props = {
      id: field.id,
      key: field.id,
      form: `metadatafield${field.id}`,
      onChange: this.handleChangeField.bind(this),
      validate: this.metadataFieldValidation.bind(this),
      onDelete: this.handleDeleteField.bind(this),
      onClone: this.handleCloneField.bind(this),
      field: field
    };

    if (field.type === MetadataService.Fields.TEXT) {
      return (
        <MetadataFields.TextField
          {...props}
          initialValues={{
            name: field.values.name,
            value: field.values.value,
            role: field.values.role
          }}
        />
      );
    }

    if (field.type === MetadataService.Fields.NUMBER) {
      return (
        <MetadataFields.NumberField
          {...props}
          initialValues={{
            name: field.values.name,
            value: field.values.value,
            role: field.values.role
          }}
        />
      );
    }

    if (field.type === MetadataService.Fields.COST) {
      return (
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
      return (
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

    if (field.type === MetadataService.Fields.RANGE) {
      return (
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
      return (
        <MetadataFields.SwitchField
          {...props}
          initialValues={{
            name: field.values.name,
            from: field.values.from,
            to: field.values.to
          }}
        />
      );
    }

    if (field.type === MetadataService.Fields.DATE) {
      return (
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
      return (
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

    if (field.type === MetadataService.Fields.UNIT) {
      return (
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
      return (
        <MetadataFields.ContactField
          {...props}
          onEventsChange={this.props.onEventsChange}
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

    if (field.type === MetadataService.Fields.ADDRESS) {
      return (
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

  });

  SortableList = SortableContainer(({items}) => {
    return (
      <div>
        {items.map((value, index) => {
          return (
            <this.SortableItem key={`item-${value.id}`} index={index} value={value}/>
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
          name: `${cloned.values.name} Copy`
        }
      }
    ];

    const originalIndex = _.findIndex(fields, {id: id});

    this.props.onFieldsChange(
      arrayMove(fields, fields.length - 1, originalIndex + 1)
    );
  }

  addMetadataField(params) {

    const nextId = this.props.fields.reduce((acc, value) => (
        acc < value.id ? value.id : acc
      ), this.props.fields.length ? this.props.fields[0].id : 0) + 1;


    this.props.onFieldsChange([
      ...this.props.fields,
      {
        id: nextId,
        type: params.type,
        values: {
          role: MetadataRolesDefault,
          name: '',
          value: '',
          ...params.values
        }
      }
    ]);
  }

  handleDeleteField(key) {
    this.props.onFieldsChange(this.props.fields.filter((field) => field.id !== key));
  }

  onSortEnd({oldIndex, newIndex}) {

    const staticMetadataFieldsCount = filterDynamicMetadataFields(this.props.fields).length;

    this.props.onFieldsChange(
      arrayMove(this.props.fields, oldIndex + staticMetadataFieldsCount, newIndex + staticMetadataFieldsCount)
    );

  }

  getStaticFields() {

    const fields = filterHardcodedMetadataFields(this.props.fields);

    const elements = [];

    fields.forEach((field) => {

      if (!field.values.name) return false;

      const props = {
        id: field.id,
        key: field.id,
        form: `metadatafield${field.id}`,
        onChange: this.handleChangeField.bind(this),
        validate: this.metadataFieldValidation.bind(this),
        onDelete: this.handleDeleteField.bind(this),
        onClone: this.handleCloneField.bind(this),
        field: field,
        tools: false,
        initialValues: {
          name: field.values.name,
          value: field.values.value,
          role: field.values.role
        }
      };

      if (field.values.name && field.values.name === hardcodedRequiredMetadataFieldsNames.LocationName) {
        elements.push(
          <MetadataFields.LocationField {...props}/>
        );
      }

      if (field.values.name && field.values.name === hardcodedRequiredMetadataFieldsNames.DeviceOwner) {
        elements.push(
          <MetadataFields.DeviceOwnerField {...props}/>
        );
      }

      if (field.values.name && field.values.name === hardcodedRequiredMetadataFieldsNames.DeviceName) {
        elements.push(
          <MetadataFields.DeviceNameField {...props}/>
        );
      }

    });

    return elements;

  }

  render() {

    return (
      <div>
        <Metadata.ItemsList>
          { this.getStaticFields()}

          { this.props.fields && this.props.fields.length && (
            <this.SortableList items={filterDynamicMetadataFields(this.props.fields)}
                               onSortEnd={this.onSortEnd.bind(this)}
                               useDragHandle={true}
                               lockAxis="y"
                               helperClass="product-metadata-item-drag-active"/>) || null
          }

        </Metadata.ItemsList>
        <AddMetadataFields onFieldAdd={this.addMetadataField.bind(this)}/>
      </div>
    );
  }
}

export default ProductMetadata;
