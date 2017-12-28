import React from 'react';
import {AddMetadataFields} from 'scenes/Products/components/AddField';
import {BackTop} from 'components';
import {
  Metadata as MetadataService,
  // filterDynamicMetadataFields,
  filterHardcodedMetadataFields as getHardcodedMetadataFields,
  hardcodedRequiredMetadataFieldsNames
} from 'services/Products';
import Metadata from "scenes/Products/components/Metadata";
// import {MetadataRolesDefault} from 'services/Roles';
import _ from 'lodash';
import {
  SortableContainer,
  SortableElement,
  // arrayMove
} from 'react-sortable-hoc';
// import Scroll from 'react-scroll';

import ImmutablePropTypes from 'react-immutable-proptypes';
import PropTypes from 'prop-types';

const MetadataFields = Metadata.Fields;
class ProductMetadata extends React.Component {

  static propTypes = {

    fields: React.PropTypes.array,

    formValues: ImmutablePropTypes.contains({
      metaFields: ImmutablePropTypes.contains({
        id: PropTypes.oneOfType([
          PropTypes.string,
          PropTypes.number
        ]),
        type: PropTypes.string,
        name: PropTypes.string,
        role: PropTypes.string,
        isDefault: PropTypes.bool,
        value: PropTypes.oneOfType([
          PropTypes.string,
          PropTypes.number
        ])
      })
    }),
  };

  constructor(props) {
    super(props);

    this.onSortEnd = this.onSortEnd.bind(this);
    this.onSortStart = this.onSortStart.bind(this);
    this.handleCloneField = this.handleCloneField.bind(this);
    this.addMetadataField = this.addMetadataField.bind(this);
    this.handleChangeField = this.handleChangeField.bind(this);
    this.handleDeleteField = this.handleDeleteField.bind(this);
    this.metadataFieldValidation = this.metadataFieldValidation.bind(this);

  }

  state = {
    isSortEnabled: false
  };

  componentDidUpdate() {
    // this.props.fields.forEach((field) => {
    //   if (field && field.values && field.values.isRecentlyCreated) {
    //
    //     Scroll.scroller.scrollTo(`${field.name}`, {
    //       duration: 1000,
    //       offset: -64,
    //       smooth: "easeInOutQuint",
    //     });
    //
    //     this.handleChangeField({
    //       ...field,
    //       values: {
    //         ...field.values,
    //         isRecentlyCreated: false
    //       }
    //     });
    //   }
    // });
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

  SortableItem = SortableElement(({value}) => {

    const field = value;

    let element;

    const props = {
      id: field.id,
      key: field.id,
      form: `metadatafield${field.id}`,
      onChange: this.handleChangeField,
      validate: this.metadataFieldValidation,
      onDelete: this.handleDeleteField,
      onClone: this.handleCloneField,
      field: field
    };

    if (field.type === MetadataService.Fields.TEXT) {
      element = (
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
      element = (
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

    if (field.type === MetadataService.Fields.RANGE) {
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

    if (field.type === MetadataService.Fields.DATE) {
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

    if (field.type === MetadataService.Fields.UNIT) {
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

    if (field.type === MetadataService.Fields.ADDRESS) {
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

    return element;

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

    const isNameAlreadyExists = (name) => {
      return this.props.fields.some((field) => {
        return field.values.name.trim() === name.trim();
      });
    };

    const cloned = _.find(this.props.fields, {id: id});

    // const nextId = _.random(4, 2000000000);

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

    // const fields = [
    //   ...this.props.fields,
    //   {
    //     ...cloned,
    //     id: nextId,
    //     values: {
    //       ...cloned.values,
    //       name: `${name}`,
    //       isSavedBefore: false,
    //       isRecentlyCreated: true
    //     }
    //   }
    // ];

    // const originalIndex = _.findIndex(fields, {id: id});

    // this.props.onFieldsChange(
    //   arrayMove(fields, fields.length - 1, originalIndex + 1)
    // );
  }

  addMetadataField(/*params*/) {

    const nextId = _.random(4, 2000000000);

    // this.props.onFieldsChange([
    //   ...this.props.fields,
    //   {
    //     id: nextId,
    //     type: params.type,
    //     values: {
    //       role: MetadataRolesDefault,
    //       name: '',
    //       value: '',
    //       ...params.values,
    //       isRecentlyCreated: true
    //     }
    //   }
    // ]);

    /** @todo dirty hack, remove it after refactoring */
    setTimeout(() => document.querySelector(`.metadata-name-field-${nextId}  input`).focus(), 100);
  }

  handleDeleteField(/*key*/) {
    // this.props.onFieldsChange(this.props.fields.filter((field) => field.id !== key));
  }

  onSortEnd(/*{oldIndex, newIndex}*/) {

    // const staticMetadataFieldsCount = getHardcodedMetadataFields(this.props.fields).length;

    // this.props.onFieldsChange(
    //   arrayMove(this.props.fields, oldIndex + staticMetadataFieldsCount, newIndex + staticMetadataFieldsCount)
    // );

    this.setState({
      isSortEnabled: false
    });

  }

  getStaticFields() {

    const fields = getHardcodedMetadataFields(this.props.formValues.get('metaFields'));

    const elements = [];

    fields.forEach((field, key) => {

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

  render() {

    return (
      <div className={this.state.isSortEnabled ? 'no-mouse-selection' : null}>
        <Metadata.ItemsList>
          { this.getStaticFields()}

          {/*{ this.props.fields && this.props.fields.length && (*/}
            {/*<this.SortableList items={filterDynamicMetadataFields(this.props.fields)}*/}
                               {/*useWindowAsScrollContainer={true}*/}
                               {/*onSortEnd={this.onSortEnd}*/}
                               {/*onSortStart={this.onSortStart}*/}
                               {/*useDragHandle={true}*/}
                               {/*lockAxis="y"*/}
                               {/*helperClass="product-metadata-item-drag-active"/>) || null*/}
          {/*}*/}

        </Metadata.ItemsList>
        <AddMetadataFields onFieldAdd={this.addMetadataField}/>
        <BackTop/>
      </div>
    );
  }
}

export default ProductMetadata;
