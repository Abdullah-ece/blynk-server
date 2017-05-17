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
    DeviceNameField
  }
};

export default Metadata;
