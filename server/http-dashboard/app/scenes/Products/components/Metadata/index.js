import './styles.less';
import Item from './components/Item';
import ItemsList from './components/ItemsList';

import TextField from './components/TextField';
import NumberField from './components/NumberField';
import CostField from './components/CostField';
import TimeField from './components/TimeField';
import ShiftField from './components/ShiftField';
import CoordinatesField from './components/CoordinatesField';
import UnitField from './components/UnitField';

const Metadata = {
  Item: Item,
  ItemsList: ItemsList,
  Fields: {
    TextField,
    NumberField,
    CostField,
    TimeField,
    ShiftField,
    CoordinatesField,
    UnitField
  }
};

export default Metadata;
