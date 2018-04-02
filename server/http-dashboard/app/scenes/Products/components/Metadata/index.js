import './styles.less';
import Item from './components/Item';
import ItemsList from './components/ItemsList';

import TextField from './components/TextField';
import NumberField from './components/NumberField';
import CostField from './components/CostField';
import TimeField from './components/TimeField';
import ShiftField from './components/ShiftField';
import SwitchField from './components/SwitchField';
import DateField from './components/DateField';
import CoordinatesField from './components/CoordinatesField';
import UnitField from './components/UnitField';
import ContactField from './components/ContactField';
import AddressField from './components/AddressField';
import DeviceNameField from './components/DeviceNameField';
import LocationField from './components/LocationField';
import DeviceOwnerField from './components/DeviceOwnerField';
import ManufacturerField from './components/ManufacturerField';
import ModelNameField from './components/ModelNameField';
import TimezoneOfDeviceField from './components/TimezoneOfDeviceField';
import DeviceReferenceField from './components/DeviceReferenceField';

const Metadata = {
  Item: Item,
  ItemsList: ItemsList,
  Fields: {
    TextField,
    NumberField,
    CostField,
    TimeField,
    ShiftField,
    DateField,
    CoordinatesField,
    UnitField,
    SwitchField,
    ContactField,
    AddressField,
    DeviceNameField,
    LocationField,
    DeviceOwnerField,
    ManufacturerField,
    ModelNameField,
    TimezoneOfDeviceField,
    DeviceReferenceField
  }
};

export default Metadata;
